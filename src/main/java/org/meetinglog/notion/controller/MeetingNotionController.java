package org.meetinglog.notion.controller;

import lombok.RequiredArgsConstructor;
import org.meetinglog.notion.dto.NotionUploadRequest;
import org.meetinglog.notion.service.MeetingNotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/meeting/notion")
public class MeetingNotionController {

    private final MeetingNotionService meetingNotionService;

    @PostMapping("/{meetingId}")
    public ResponseEntity<String> pushToNotion(
            @PathVariable Long meetingId,
            @RequestBody NotionUploadRequest request
    ) {
        meetingNotionService.pushMeetingToNotion(meetingId, request.getParentPageUrl());
        return ResponseEntity.ok("Notion에 회의록이 성공적으로 업로드되었습니다.");
    }

}

