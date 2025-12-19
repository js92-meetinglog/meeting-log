
package org.meetinglog.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.ai.dto.TranscribeAndSummarizeResponse;
import org.meetinglog.meeting.dto.MeetingQaRequest;
import org.meetinglog.meeting.dto.MeetingQaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiTranscribeClient {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    private final RestTemplate restTemplate;


    public TranscribeAndSummarizeResponse requestAi(byte[] audioBytes, String filename) {

        ByteArrayResource resource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio_file", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            log.info("AI 서버 호출 시작 → {}", aiServerUrl);
            String aiUrl = aiServerUrl + "/api/v1/transcribe-and-summarize";
            ResponseEntity<TranscribeAndSummarizeResponse> response =
                    restTemplate.postForEntity(aiUrl, entity, TranscribeAndSummarizeResponse.class);

            log.info("AI 서버 응답: {}", response.getStatusCode());

            return response.getBody();
        } catch (Exception e) {
            log.error("AI 서버 호출 실패: {}", e.getMessage(), e);
            throw new RuntimeException("AI 서버 호출 중 오류", e);
        }
    }

    /**
     * 회의 내용 텍스트 기반 질의응답 요청 (RAG/LangChain/GPT)
     * @param request 회의 텍스트와 질문이 포함된 요청 객체
     * @return AI 서버로부터 받은 답변 및 근거 텍스트
     */
    public MeetingQaResponse requestQa(MeetingQaRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MeetingQaRequest> entity = new HttpEntity<>(request, headers);

        try {
            String aiUrl = aiServerUrl + "/api/v1/qa";

            ResponseEntity<MeetingQaResponse> response =
                    restTemplate.postForEntity(aiUrl, entity, MeetingQaResponse.class);

            if (response.getBody() == null) {
                throw new RuntimeException("AI 서버 응답이 비어있습니다.");
            }

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("AI 서버 질의응답 호출 중 오류", e);
        }
    }
}
