package org.meetinglog.meeting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingParticipantRequest {
    private Long userId;
    private String userName;
}

