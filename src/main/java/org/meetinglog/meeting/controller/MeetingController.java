package org.meetinglog.meeting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.jpa.entity.MeetingDtl;
import org.meetinglog.jpa.entity.MeetingMst;
import org.meetinglog.jpa.repository.MeetingDtlRepository;
import org.meetinglog.jpa.repository.MeetingMstRepository;
import org.meetinglog.meeting.dto.*;
import org.meetinglog.common.dto.ApiResponse;
import org.meetinglog.common.enums.ErrorMessage;
import org.meetinglog.common.enums.SuccessMessage;
import org.meetinglog.common.exception.SearchException;
import org.meetinglog.meeting.service.AiProcessService;
import org.meetinglog.meeting.service.MeetingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/meeting")
public class MeetingController {

    private final MeetingService meetingService;
    private final AiProcessService aiProcessService;
    private final MeetingDtlRepository meetingDtlRepository;
    private final MeetingMstRepository meetingMstRepository;



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

    @PostMapping("/stt")
    public ApiResponse<Long> uploadAndProcessStt(@RequestParam("file") MultipartFile file) {

        Long meetingId = meetingService.createMeetingFromFile(file);

        // 비동기 AI 처리 시작
        aiProcessService.processAudioAsync(meetingId);

        return ApiResponse.success(meetingId, "AI 음성 인식 및 요약 작업이 진행 중입니다.");
    }

    @PostMapping("/audio")
    public ApiResponse<MeetingAudioResponse> uploadAudio(
            @ModelAttribute MeetingAudioRequest request
    ){
        Long meetingId = meetingService.createMeetingFromFile(request.getAudioFile());

        // 비동기 처리 시작
        aiProcessService.processAudioAsync(meetingId);

        MeetingAudioResponse response = MeetingAudioResponse.builder()
                .meetingId(meetingId)
                .status("PROCESSING")
                .build();

        return ApiResponse.success(response, "AI 음성 인식 및 요약 작업이 시작되었습니다.");
    }

    @PostMapping("/mock-ai")
    public ApiResponse<String> mockAiUpdate(
            @RequestParam Long meetingId,
            @RequestBody AiSttResponse ai
    ) {

        MeetingDtl dtl = meetingDtlRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의 없음"));

        dtl.setMeetingStt(ai.getTranscript());
        dtl.setMeetingSummary(ai.getSummary());
        dtl.setKeyPoints(String.join("\n", ai.getKey_points()));
        dtl.setActionItems(String.join("\n", ai.getAction_items()));
        dtl.setLanguage(ai.getLanguage());

        meetingDtlRepository.save(dtl);

        MeetingMst mst = dtl.getMeeting();
        mst.setMeetingState("DONE");
        meetingMstRepository.save(mst);

        return ApiResponse.success("MOCK AI 저장 완료");
    }




}
