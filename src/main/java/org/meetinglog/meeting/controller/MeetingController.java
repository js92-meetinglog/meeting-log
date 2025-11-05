package org.meetinglog.meeting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.dto.MeetingSummaryRequest;
import org.meetinglog.meeting.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting")
public class MeetingController {

    private final MeetingService meetingService;

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

    // 회의 녹음 파일 처리
    /*@PostMapping("/{meetingId}/recording")
    public ResponseEntity<String> processMeetingRecording(@PathVariable Long meetingId, @RequestParam("file") MultipartFile file) {
        // AI 시스템에 파일을 보내고 텍스트 변환 요청
        String aiText = aiService.processRecording(file);

        // 변환된 텍스트를 DB에 저장
        meetingService.saveMeetingText(meetingId, aiText);

        return ResponseEntity.ok("회의 녹음이 텍스트로 변환되어 저장되었습니다.");
    }

    // 회의 텍스트 요약
    @PostMapping("/{meetingId}/summary")
    public ResponseEntity<String> summarizeMeetingText(@PathVariable Long meetingId, @RequestBody MeetingSummaryRequest request) {
        // AI 시스템에 텍스트 요약 요청
        String summarizedText = aiService.summarizeText(request.getText());

        // 요약된 텍스트를 DB에 저장
        meetingService.saveMeetingSummary(meetingId, summarizedText);

        return ResponseEntity.ok("회의 텍스트가 요약되어 저장되었습니다.");
    }*/

}
