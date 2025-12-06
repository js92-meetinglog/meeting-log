package org.meetinglog.meeting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingParticipantRequest {
    private String unqId;
    private String userName;
}

