package org.opensearch.dataprepper.plugins.mongo.documentdb;

import org.opensearch.dataprepper.common.concurrent.BackgroundThreadFactory;
import org.opensearch.dataprepper.metrics.PluginMetrics;
import org.opensearch.dataprepper.model.acknowledgements.AcknowledgementSetManager;
import org.opensearch.dataprepper.model.buffer.Buffer;
import org.opensearch.dataprepper.model.event.Event;
import org.opensearch.dataprepper.model.record.Record;
import org.opensearch.dataprepper.model.source.coordinator.enhanced.EnhancedSourceCoordinator;
import org.opensearch.dataprepper.plugins.mongo.configuration.CollectionConfig;
import org.opensearch.dataprepper.plugins.mongo.export.MongoDBExportPartitionSupplier;
import org.opensearch.dataprepper.plugins.mongo.configuration.MongoDBSourceConfig;
import org.opensearch.dataprepper.plugins.mongo.export.ExportScheduler;
import org.opensearch.dataprepper.plugins.mongo.export.ExportWorker;
import org.opensearch.dataprepper.plugins.mongo.leader.LeaderScheduler;
import org.opensearch.dataprepper.plugins.mongo.s3partition.S3PartitionCreatorScheduler;
import org.opensearch.dataprepper.plugins.mongo.stream.StreamScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DocumentDBService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentDBService.class);
    private final EnhancedSourceCoordinator sourceCoordinator;
    private final PluginMetrics pluginMetrics;
    private final MongoDBSourceConfig sourceConfig;
    private final AcknowledgementSetManager acknowledgementSetManager;
    private ExecutorService executor;
    private final MongoDBExportPartitionSupplier mongoDBExportPartitionSupplier;
    public DocumentDBService(final EnhancedSourceCoordinator sourceCoordinator,
                             final MongoDBSourceConfig sourceConfig,
                             final PluginMetrics pluginMetrics,
                             final AcknowledgementSetManager acknowledgementSetManager) {
        this.sourceCoordinator = sourceCoordinator;
        this.pluginMetrics = pluginMetrics;
        this.acknowledgementSetManager = acknowledgementSetManager;
        this.sourceConfig = sourceConfig;
        this.mongoDBExportPartitionSupplier = new MongoDBExportPartitionSupplier(sourceConfig);
    }

    /**
     * This service start three long-running threads (scheduler)
     * Each thread is responsible for one type of job.
     * The data will be guaranteed to be sent to {@link Buffer} in order.
     *
     * @param buffer Data Prepper Buffer
     */
    public void start(Buffer<Record<Event>> buffer) {
        final List<Runnable> runnableList = new ArrayList<>();

        final LeaderScheduler leaderScheduler = new LeaderScheduler(sourceCoordinator, sourceConfig.getCollections());
        runnableList.add(leaderScheduler);

        if (sourceConfig.getCollections().stream().anyMatch(CollectionConfig::isExportEnabled)) {
            final ExportScheduler exportScheduler = new ExportScheduler(sourceCoordinator, mongoDBExportPartitionSupplier, pluginMetrics);
            final ExportWorker exportWorker = new ExportWorker(sourceCoordinator, buffer, pluginMetrics, acknowledgementSetManager, sourceConfig);
            runnableList.add(exportScheduler);
            runnableList.add(exportWorker);
        }

        if (sourceConfig.getCollections().stream().anyMatch(CollectionConfig::isStreamEnabled)) {
            final S3PartitionCreatorScheduler s3PartitionCreatorScheduler = new S3PartitionCreatorScheduler(sourceCoordinator);
            runnableList.add(s3PartitionCreatorScheduler);
            final StreamScheduler streamScheduler = new StreamScheduler(sourceCoordinator, buffer, acknowledgementSetManager, sourceConfig, pluginMetrics);
            runnableList.add(streamScheduler);
        }

        executor = Executors.newFixedThreadPool(runnableList.size(), BackgroundThreadFactory.defaultExecutorThreadFactory("documentdb-source"));
        runnableList.forEach(executor::submit);
    }

    /**
     * Interrupt the running of schedulers.
     * Each scheduler must implement logic for gracefully shutdown.
     */
    public void shutdown() {
        if (executor != null) {
            LOG.info("shutdown DocumentDB Service scheduler and worker");
            executor.shutdownNow();
        }
    }
}
