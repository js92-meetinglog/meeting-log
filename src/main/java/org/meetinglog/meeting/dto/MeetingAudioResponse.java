package org.meetinglog.meeting.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MeetingAudioResponse {

    private Long meetingId;

    private String transcript;
    private String summary;

    private List<String> keyPoints;
    private List<String> actionItems;

    private String language;

    private String status; // PROCESSING / DONE
}
