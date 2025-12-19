package org.meetinglog.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.common.dto.ApiResponse;
import org.meetinglog.common.enums.ErrorMessage;
import org.meetinglog.common.enums.SuccessMessage;
import org.meetinglog.common.exception.SearchException;
import org.meetinglog.jpa.repository.MeetingDtlRepository;
import org.meetinglog.jpa.repository.MeetingMstRepository;
import org.meetinglog.meeting.dto.*;
import org.meetinglog.meeting.service.AiProcessService;
import org.meetinglog.meeting.service.MeetingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

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


    @PostMapping
    public ApiResponse<Long> createMeeting(@RequestBody MeetingMstRequest request) {
        Long id = meetingService.createMeeting(request);
        return ApiResponse.success(id, "회의가 등록되었습니다.");
    }

    @GetMapping("/{meetingId}")
    public ApiResponse<MeetingAudioResponse> getMeeting(@PathVariable Long meetingId) {
        MeetingAudioResponse res = meetingService.getMeeting(meetingId);
        return ApiResponse.success(res, "회의 상세 조회 성공");
    }

    @GetMapping("/list")
    public ApiResponse<List<MeetingListResponse>> getMeetingList() {

        List<MeetingListResponse> list = meetingService.getMeetingList();
        return ApiResponse.success(list, "회의 전체 목록 조회 성공");
    }



    @PostMapping("/{meetingId}/audio")
    public ApiResponse<String> uploadAudio(
            @PathVariable Long meetingId,
            @RequestParam MultipartFile file
    ) {
        meetingService.attachAudioToMeeting(meetingId, file);

        aiProcessService.executeAsync(meetingId);

        return ApiResponse.success("PROCESSING", "AI 분석이 시작되었습니다.");
    }

    @PutMapping("/{meetingId}")
    public ApiResponse<String> updateMeeting(
            @PathVariable Long meetingId,
            @RequestBody MeetingUpdateRequest request
    ) {

        meetingService.updateMeeting(meetingId, request);

        return ApiResponse.success("회의가 성공적으로 수정되었습니다.");
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
            if (page < 0) throw new SearchException(ErrorMessage.INVALID_PAGE_NUMBER.getMessage());
            if (size <= 0 || size > 100) throw new SearchException(ErrorMessage.INVALID_PAGE_SIZE.getMessage());
            if (startDate != null && endDate != null && startDate.isAfter(endDate))
                throw new SearchException(ErrorMessage.INVALID_DATE_RANGE.getMessage());

            MeetingSearchResponse response =
                    meetingService.searchMeetings(keyword, participants, startDate, endDate, page, size);

            return ApiResponse.success(response, SuccessMessage.SEARCH_COMPLETED.getMessage());

        } catch (Exception e) {
            log.error("회의록 검색 중 오류 발생: ", e);
            throw new SearchException(ErrorMessage.SEARCH_ERROR.getMessage(), e);
        }
    }


    @PostMapping("/mock-ai")
    public ApiResponse<String> mockAiUpdate(
            @RequestParam Long meetingId,
            @RequestBody AiSttResponse ai
    ) {
        var dtl = meetingDtlRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의 없음"));

        dtl.setMeetingStt(ai.getTranscript());
        dtl.setMeetingSummary(ai.getSummary());
        dtl.setKeyPoints(String.join("\n", ai.getKey_points()));
        dtl.setActionItems(String.join("\n", ai.getAction_items()));
        dtl.setLanguage(ai.getLanguage());

        meetingDtlRepository.save(dtl);

        var mst = dtl.getMeeting();
        mst.setMeetingState("DONE");
        meetingMstRepository.save(mst);

        return ApiResponse.success("MOCK AI 저장 완료");
    }

  @GetMapping("/{meetingId}/file-download")
  public ResponseEntity<Resource> getMeetingFile(@PathVariable Long meetingId) {
    return meetingService.getMeetingFile(meetingId);
  }

  /**
   * 회의 내용 텍스트 기반 질의응답 (RAG/LangChain/GPT 기반)
   * @param meetingId 회의 ID
   * @param question 회의 내용에 대한 질문
   * @return 답변 및 근거 텍스트
   */
  @Operation(
    summary = "회의 내용 질의응답",
    description = "RAG/LangChain/GPT 기반으로 회의 내용에 대한 질문에 답변합니다. 회의록 STT가 완료된 회의에 대해서만 사용 가능합니다. 회의 텍스트는 서버에서 자동으로 조회됩니다."
  )
  @GetMapping("/{meetingId}/qa")
  public ApiResponse<MeetingQaResponse> askMeetingQuestion(
          @PathVariable Long meetingId,
          @RequestParam String question
  ) {
      try {
          if (question == null || question.trim().isEmpty()) {
              return ApiResponse.error(ErrorMessage.QA_QUESTION_REQUIRED.getMessage());
          }

          MeetingQaResponse response = aiProcessService.askQuestion(meetingId, question);
          return ApiResponse.success(response);

      } catch (IllegalArgumentException e) {
          log.error("잘못된 요청: {}", e.getMessage());
          return ApiResponse.error(e.getMessage());

      } catch (Exception e) {
          log.error("질의응답 처리 중 오류 발생: ", e);
          return ApiResponse.error(ErrorMessage.QA_PROCESSING_ERROR.getMessage());
      }
  }
}
