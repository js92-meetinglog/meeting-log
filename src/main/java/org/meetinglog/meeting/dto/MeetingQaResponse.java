
package org.meetinglog.meeting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 회의 내용 텍스트 기반 질의응답 응답 DTO
 * @param answer 질문에 대한 답변
 * @param sourceContext 답변의 근거가 된 원문 텍스트 (선택적)
 */
@Schema(description = "회의 내용 질의응답 응답")
public record MeetingQaResponse(
        @Schema(description = "질문에 대한 답변", example = "이번 회의의 주요 결정사항은 다음과 같습니다:\n1. 신규 프로젝트 일정을 2주 앞당기기로 결정\n2. 개발팀 인원 2명 추가 배치")
        @JsonProperty("answer") 
        String answer,
        
        @Schema(description = "답변의 근거가 된 원문 텍스트", example = "회의 중반부에서 팀장님께서 '프로젝트 일정을 2주 앞당기고, 개발팀 인원을 2명 추가 배치하겠습니다'라고 말씀하셨습니다.", nullable = true)
        @JsonProperty("source_context") 
        String sourceContext
) {}
