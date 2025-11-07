package org.meetinglog.meeting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.service.AiService;
import org.meetinglog.meeting.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/meeting")
public class MeetingController {

    private final MeetingService meetingService;
    private final AiService aiService;

    @GetMapping("/test")
    public ResponseEntity<String> testApi() {
        return ResponseEntity.ok().body("test !! ");
    }

    // 회의 생성
    @PostMapping
    public ResponseEntity<String> createMeeting(@RequestBody MeetingMstRequest request) {
        Long id = meetingService.createMeeting(request);
        return ResponseEntity.ok("회의가 등록되었습니다. ID=" + id);
    }

    // 회의 조회 (단일 회의 정보 조회)
    @GetMapping("/{meetingId}")
    public ResponseEntity<?> getMeeting(@PathVariable Long meetingId) {
        var meeting = meetingService.getMeetingById(meetingId);
        if (meeting != null) {
            return ResponseEntity.ok(meeting);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 회의 수정
    @PutMapping("/{meetingId}")
    public ResponseEntity<String> updateMeeting(@PathVariable Long meetingId, @RequestBody MeetingMstRequest request) {
        boolean isUpdated = meetingService.updateMeeting(meetingId, request);
        if (isUpdated) {
            return ResponseEntity.ok("회의가 수정되었습니다. ID=" + meetingId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 회의 삭제
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(@PathVariable Long meetingId) {
        boolean isDeleted = meetingService.deleteMeeting(meetingId);
        if (isDeleted) {
            return ResponseEntity.ok("회의가 삭제되었습니다. ID=" + meetingId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 음성 파일 처리
    @PostMapping("/{meetingId}/recording")
    public Mono<ResponseEntity<String>> processAudio(@PathVariable Long meetingId, @RequestParam("file") MultipartFile file) {
        return meetingService.processRecordingAndStore(meetingId, file);
    }

    // 텍스트 요약 처리
    @PostMapping("/{meetingId}/summary")
    public Mono<ResponseEntity<String>> summarizeText(@PathVariable Long meetingId, @RequestBody String text) {
        return meetingService.summarizeTextAndStore(meetingId, text);
    }


}
