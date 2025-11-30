package org.meetinglog.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.ai.dto.TranscribeAndSummarizeResponse;
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
    private String aiServerUrl;  // http://134.185.112.112:9292/api/v1/transcribe-and-summarize

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

            ResponseEntity<TranscribeAndSummarizeResponse> response =
                    restTemplate.postForEntity(aiServerUrl, entity, TranscribeAndSummarizeResponse.class);

            log.info("AI 서버 응답: {}", response.getStatusCode());

            return response.getBody();
        } catch (Exception e) {
            log.error("AI 서버 호출 실패: {}", e.getMessage(), e);
            throw new RuntimeException("AI 서버 호출 중 오류", e);
        }
    }
}
