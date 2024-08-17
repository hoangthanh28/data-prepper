/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.dataprepper.plugins.source.rds.schema;

import org.opensearch.dataprepper.plugins.source.rds.model.BinlogCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SchemaManager {
    private static final Logger LOG = LoggerFactory.getLogger(SchemaManager.class);
    static final String COLUMN_NAME = "COLUMN_NAME";
    static final String BINLOG_STATUS_QUERY = "SHOW MASTER STATUS";
    static final String BINLOG_FILE = "File";
    static final String BINLOG_POSITION = "Position";
    static final int NUM_OF_RETRIES = 3;
    static final int BACKOFF_IN_MILLIS = 500;
    private final ConnectionManager connectionManager;

    public SchemaManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public List<String> getPrimaryKeys(final String database, final String table) {
        int retry = 0;
        while (retry <= NUM_OF_RETRIES) {
            final List<String> primaryKeys = new ArrayList<>();
            try (final Connection connection = connectionManager.getConnection()) {
                final ResultSet rs = connection.getMetaData().getPrimaryKeys(database, null, table);
                while (rs.next()) {
                    primaryKeys.add(rs.getString(COLUMN_NAME));
                }
                return primaryKeys;
            } catch (Exception e) {
                LOG.error("Failed to get primary keys for table {}, retrying", table, e);
            }
            applyBackoff();
            retry++;
        }
        LOG.warn("Failed to get primary keys for table {}", table);
        return List.of();
    }

    public Optional<BinlogCoordinate> getCurrentBinaryLogPosition() {
        int retry = 0;
        while (retry <= NUM_OF_RETRIES) {
            try (final Connection connection = connectionManager.getConnection()) {
                final Statement statement = connection.createStatement();
                final ResultSet rs = statement.executeQuery(BINLOG_STATUS_QUERY);
                if (rs.next()) {
                    return Optional.of(new BinlogCoordinate(rs.getString(BINLOG_FILE), rs.getLong(BINLOG_POSITION)));
                }
            } catch (Exception e) {
                LOG.error("Failed to get current binary log position, retrying", e);
            }
            applyBackoff();
            retry++;
        }
        LOG.warn("Failed to get current binary log position");
        return Optional.empty();
    }

    private void applyBackoff() {
        try {
            Thread.sleep(BACKOFF_IN_MILLIS);
        } catch (final InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
