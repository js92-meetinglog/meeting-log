package org.meetinglog.meeting.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetingUpdateRequest {

    // --- 회의 기본 정보 ---
    private String meetingName;
    private LocalDateTime meetingDate;
    private Integer meetingDuration;
    private String meetingState;

    // --- 참석자 수정 ---
    private List<MeetingParticipantRequest> participants;

    // --- 상세 ---
    private String meetingStt;      // 텍스트
    private String meetingSummary;  // 요약
    private List<String> keyPoints; // 핵심 내용 리스트
    private List<String> actionItems; // 액션아이템 리스트
    private String language;        // 감지된 언어
}
