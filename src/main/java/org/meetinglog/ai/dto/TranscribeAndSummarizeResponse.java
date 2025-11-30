package org.meetinglog.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TranscribeAndSummarizeResponse(
        @JsonProperty("transcript") String transcript,
        @JsonProperty("summary") String summary,
        @JsonProperty("key_points") List<String> keyPoints,
        @JsonProperty("action_items") List<String> actionItems,
        @JsonProperty("language") String language
) {}
