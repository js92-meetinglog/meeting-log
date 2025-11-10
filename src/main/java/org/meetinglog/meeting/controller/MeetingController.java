package org.meetinglog.meeting.controller;

import lombok.extern.slf4j.Slf4j;
import org.meetinglog.meeting.dto.MeetingSearchResponse;
import org.meetinglog.meeting.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

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
    
    @GetMapping("/search")
    public ResponseEntity<MeetingSearchResponse> searchMeetings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> participants,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            MeetingSearchResponse response = meetingService.searchMeetings(keyword, participants, startDate, endDate, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("회의록 검색 중 오류 발생: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
