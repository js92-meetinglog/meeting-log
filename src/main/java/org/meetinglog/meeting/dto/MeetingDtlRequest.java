package org.meetinglog.meeting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingDtlRequest {
    private Long fileId;           // FILE_ID (nullable)
    private String meetingSummary; // 회의 요약
    private String meetingStt;     // STT 텍스트
}
