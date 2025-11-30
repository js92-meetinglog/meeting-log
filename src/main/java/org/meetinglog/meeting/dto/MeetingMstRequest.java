package org.meetinglog.meeting.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MeetingMstRequest {
    private String meetingName;       // 회의명
    //private String meetingState;      // 상태 (예: 진행중, 종료 등)
    private List<MeetingParticipantRequest> participants;
    //private Integer meetingDuration;   // 회의 소요시간(분)
    //private MeetingDtlRequest detail;  // 상세정보 (옵션)

}
