package org.meetinglog.meeting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<String> createMeeting(@RequestBody MeetingMstRequest request) {
        Long id = meetingService.createMeeting(request);
        return ResponseEntity.ok("회의가 등록되었습니다. ID=" + id);
    }

}
