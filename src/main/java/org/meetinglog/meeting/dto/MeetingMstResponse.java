package org.meetinglog.meeting.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MeetingMstResponse {
    private Long meetingId;        // 회의 ID
    private String meetingName;    // 회의명
    private String meetingState;   // 회의 상태
    private LocalDateTime meetingDate;  // 회의 일시
    private Integer meetingDuration;    // 회의 소요 시간
    private MeetingDtlResponse detail;  // 회의 상세 정보 (옵션)

    // 회의 상세 DTO
    @Getter
    @Setter
    public static class MeetingDtlResponse {
        private String meetingSummary;  // 회의 요약
        private String meetingStt;      // 회의 STT
    }
}
