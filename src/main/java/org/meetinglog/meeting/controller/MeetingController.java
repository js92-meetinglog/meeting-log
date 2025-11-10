package org.meetinglog.meeting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.common.dto.ApiResponse;
import org.meetinglog.common.enums.ErrorMessage;
import org.meetinglog.common.enums.SuccessMessage;
import org.meetinglog.common.exception.SearchException;
import org.meetinglog.meeting.dto.MeetingSearchResponse;
import org.meetinglog.meeting.service.MeetingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/meeting")
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

    @GetMapping("/search")
    public ApiResponse<MeetingSearchResponse> searchMeetings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> participants,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            if (page < 0) {
                throw new SearchException(ErrorMessage.INVALID_PAGE_NUMBER.getMessage());
            }
            if (size <= 0 || size > 100) {
                throw new SearchException(ErrorMessage.INVALID_PAGE_SIZE.getMessage());
            }
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new SearchException(ErrorMessage.INVALID_DATE_RANGE.getMessage());
            }

            MeetingSearchResponse response = meetingService.searchMeetings(keyword, participants, startDate, endDate, page, size);
            return ApiResponse.success(response, SuccessMessage.SEARCH_COMPLETED.getMessage());
        } catch (SearchException e) {
            throw e;
        } catch (Exception e) {
            log.error("회의록 검색 중 오류 발생: ", e);
            throw new SearchException(ErrorMessage.SEARCH_ERROR.getMessage(), e);
        }
    }

}
