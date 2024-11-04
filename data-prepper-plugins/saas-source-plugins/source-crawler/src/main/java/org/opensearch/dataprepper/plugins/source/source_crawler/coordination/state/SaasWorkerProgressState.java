/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.dataprepper.plugins.source.source_crawler.coordination.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SaasWorkerProgressState {

    @JsonProperty("totalItems")
    private int totalItems;

    @JsonProperty("loadedItems")
    private int loadedItems;

    @JsonProperty("exportStartTime")
    private Instant exportStartTime;

    private Map<String, Object> keyAttributes = new HashMap<>();

    @JsonProperty("itemIds")
    private List<String> itemIds;

}
