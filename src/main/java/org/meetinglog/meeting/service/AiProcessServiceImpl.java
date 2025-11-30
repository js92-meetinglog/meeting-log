package org.meetinglog.meeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiProcessServiceImpl implements AiProcessService {

    private final MeetingAudioService meetingAudioService;

    @Async
    @Override
    public void executeAsync(Long meetingId) {
        log.info("AI 비동기 처리 시작 → {}", meetingId);
        meetingAudioService.processMeetingAudio(meetingId);
    }
}
