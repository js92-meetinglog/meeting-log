package org.meetinglog.meeting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingListResponse {

    private Long meetingId;
    private String meetingName;
    private String meetingState;

    private String meetingDate;     // yyyy-MM-dd
    private String meetingTime;     // HH:mm
    private Integer meetingDuration;

    private Integer participantCount;

    private String meetingSummary;
}
