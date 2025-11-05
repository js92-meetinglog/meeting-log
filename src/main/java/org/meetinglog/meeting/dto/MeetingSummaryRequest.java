package org.meetinglog.meeting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingSummaryRequest {
    private String text;  // 요약할 텍스트
}
