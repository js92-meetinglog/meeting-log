package org.meetinglog.notion.controller;

import lombok.RequiredArgsConstructor;
import org.meetinglog.notion.service.MeetingNotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/meeting/notion")
public class MeetingNotionController {

    private final MeetingNotionService meetingNotionService;

    @PostMapping("/{meetingId}")
    public ResponseEntity<String> pushToNotion(@PathVariable Long meetingId) {
        meetingNotionService.pushMeetingToNotion(meetingId);
        return ResponseEntity.ok("Notion에 회의록이 성공적으로 업로드되었습니다.");
    }
}

