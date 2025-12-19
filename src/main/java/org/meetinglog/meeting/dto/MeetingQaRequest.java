
package org.meetinglog.meeting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 회의 내용 텍스트 기반 질의응답 요청 DTO
 * @param text 전체 회의 내용 텍스트
 * @param question 회의 내용에 대한 질문
 */
@Schema(description = "회의 내용 질의응답 요청")
public record MeetingQaRequest(
        @Schema(description = "전체 회의 내용 텍스트 (STT 결과)", example = "오늘 회의에서는 신규 프로젝트 일정에 대해 논의했습니다...")
        @JsonProperty("text") 
        String text,
        
        @Schema(description = "회의 내용에 대한 질문", example = "이번 회의의 주요 결정사항은 무엇인가요?")
        @JsonProperty("question") 
        String question
) {}
