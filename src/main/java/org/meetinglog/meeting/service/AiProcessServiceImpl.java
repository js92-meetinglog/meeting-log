package org.meetinglog.meeting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.jpa.entity.FileMst;
import org.meetinglog.jpa.entity.MeetingDtl;
import org.meetinglog.jpa.entity.MeetingMst;
import org.meetinglog.jpa.repository.FileMstRepository;
import org.meetinglog.jpa.repository.MeetingDtlRepository;
import org.meetinglog.jpa.repository.MeetingMstRepository;
import org.meetinglog.meeting.dto.AiSttResponse; // 생성 필요
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiProcessServiceImpl implements AiProcessService {

    private final MeetingDtlRepository meetingDtlRepository;
    private final MeetingMstRepository meetingMstRepository;
    private final FileMstRepository fileMstRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Async
    @Transactional
    @Override
    public void processAudioAsync(Long meetingId) {

        log.info("AI 서버 비동기 처리 시작: meetingId={}", meetingId);

        try {
            MeetingDtl dtl = meetingDtlRepository.findById(meetingId)
                    .orElseThrow(() -> new IllegalArgumentException("회의 상세정보 없음"));

            FileMst file = fileMstRepository.findById(dtl.getFile().getFileId())
                    .orElseThrow(() -> new IllegalArgumentException("파일 없음"));

            String filePath = file.getFilePath();
            log.info("AI 처리 대상 파일 경로 = {}", filePath);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("audio_file", new FileSystemResource(filePath));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            // ★★★★★ 여기서 환경변수로 가져온 URL 사용
            ResponseEntity<AiSttResponse> response =
                    restTemplate.exchange(
                            aiServerUrl,
                            HttpMethod.POST,
                            requestEntity,
                            AiSttResponse.class
                    );

            AiSttResponse ai = response.getBody();
            if (ai == null) throw new RuntimeException("AI 서버 응답이 null입니다.");

            dtl.setMeetingStt(ai.getTranscript());
            dtl.setMeetingSummary(ai.getSummary());
            dtl.setKeyPoints(String.join("\n", ai.getKey_points()));
            dtl.setActionItems(String.join("\n", ai.getAction_items()));
            dtl.setLanguage(ai.getLanguage());

            meetingDtlRepository.save(dtl);

            MeetingMst mst = dtl.getMeeting();
            mst.setMeetingState("DONE");
            meetingMstRepository.save(mst);

            log.info("AI 요약 처리 완료: meetingId={}", meetingId);

        } catch (Exception e) {
            log.error("AI 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}

