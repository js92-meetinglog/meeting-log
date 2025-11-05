package org.meetinglog.meeting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingResponse {
    private Long meetingId;
    private String message;

    public MeetingResponse(Long meetingId, String message) {
        this.meetingId = meetingId;
        this.message = message;
    }
}
