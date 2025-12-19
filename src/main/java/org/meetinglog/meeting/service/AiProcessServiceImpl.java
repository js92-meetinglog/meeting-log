package org.meetinglog.meeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.ai.AiTranscribeClient;
import org.meetinglog.common.enums.ErrorMessage;
import org.meetinglog.common.exception.BusinessException;
import org.meetinglog.jpa.entity.MeetingDtl;
import org.meetinglog.jpa.repository.MeetingDtlRepository;
import org.meetinglog.meeting.dto.MeetingQaRequest;
import org.meetinglog.meeting.dto.MeetingQaResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiProcessServiceImpl implements AiProcessService {

    private final MeetingAudioService meetingAudioService;
    private final MeetingDtlRepository meetingDtlRepository;
    private final AiTranscribeClient aiClient;

    @Async
    @Override
    public void executeAsync(Long meetingId) {
        log.info("AI 비동기 처리 시작 → {}", meetingId);
        meetingAudioService.processMeetingAudio(meetingId);
    }

    @Override
    public MeetingQaResponse askQuestion(Long meetingId, String question) {
        log.info("회의 ID: {} 에 대한 질문: {}", meetingId, question);

        MeetingDtl dtl = meetingDtlRepository.findById(meetingId)
          .orElseThrow(() -> new BusinessException(ErrorMessage.QA_MEETING_NOT_FOUND.getMessage()));

        String meetingText = dtl.getMeetingStt();
        if (meetingText == null || meetingText.trim().isEmpty()) {
            throw new BusinessException(ErrorMessage.QA_TEXT_NOT_AVAILABLE.getMessage());
        }

        MeetingQaRequest request = new MeetingQaRequest(meetingText, question);

        try {
          return aiClient.requestQa(request);

        } catch (Exception e) {
            throw new BusinessException(ErrorMessage.QA_PROCESSING_ERROR.getMessage());
        }
    }
}
