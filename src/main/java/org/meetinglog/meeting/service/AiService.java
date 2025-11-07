package org.meetinglog.meeting.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.CompletableFuture;

@Service
public class AiService {

    @Async
    public CompletableFuture<String> processAudioAndGetText(MultipartFile file) {
        // AI 서버로 음성 파일을 보내고 텍스트를 받아오는 비동기 작업
        String resultText = "Converted text from AI Server"; // 더미 텍스트
        return CompletableFuture.completedFuture(resultText);
    }

    @Async
    public CompletableFuture<String> summarizeText(String text) {
        // AI 서버에서 텍스트를 요약하는 비동기 작업
        String summary = "Summarized text from AI Server"; // 더미 요약
        return CompletableFuture.completedFuture(summary);
    }
}
