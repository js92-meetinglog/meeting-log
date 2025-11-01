package org.meetinglog.meeting.controller;

import lombok.extern.slf4j.Slf4j;
import org.meetinglog.meeting.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @GetMapping("/test")
    public ResponseEntity<String> testApi() {
        return ResponseEntity.ok().body("test !! ");
    }

    @PostMapping("/test")
    public ResponseEntity<String> testSaveApi() {

        return meetingService.testSave();
    }

}
