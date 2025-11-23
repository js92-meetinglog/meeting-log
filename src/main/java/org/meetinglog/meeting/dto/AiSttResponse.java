package org.meetinglog.meeting.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiSttResponse {
    private String transcript;
    private String summary;
    private List<String> key_points;
    private List<String> action_items;
    private String language;
}
