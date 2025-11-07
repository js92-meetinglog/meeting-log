package org.meetinglog.meeting.service;

import lombok.RequiredArgsConstructor;
import org.meetinglog.file.entity.FileMst;
import org.meetinglog.file.repository.FileMstRepository;
import org.meetinglog.meeting.dto.MeetingDtlRequest;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.dto.MeetingSummaryRequest;
import org.meetinglog.meeting.dto.SummaryAndActionItems;
import org.meetinglog.meeting.entity.MeetingDtl;
import org.meetinglog.meeting.entity.MeetingMst;
import org.meetinglog.meeting.repository.MeetingDtlRepository;
import org.meetinglog.meeting.repository.MeetingMstRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingMstRepository meetingMstRepository;
    private final MeetingDtlRepository meetingDtlRepository;
    private final FileMstRepository fileMstRepository;
    private final WebClient.Builder webClientBuilder;
    private final AiService aiService;


    // 회의 생성
    @Transactional
    public Long createMeeting(MeetingMstRequest req) {
        // 1) MEETING_MST 저장
        MeetingMst mst = MeetingMst.builder()
                .meetingName(req.getMeetingName())
                .meetingState(req.getMeetingState())
                .meetingDate(req.getMeetingDate())    // LocalDateTime
                .meetingDuration(req.getMeetingDuration())
                .build();
        mst.setFrstRgstId("system"); // ✅ 등록자 ID 세팅
        mst = meetingMstRepository.save(mst);

        // 2) (옵션) MEETING_DTL 저장 – 1:1 (PK 공유)
        if (req.getDetail() != null) {
            FileMst file = null;
            if (req.getDetail().getFileId() != null) {
                file = fileMstRepository.findById(req.getDetail().getFileId()).orElse(null);
            }

//            public ResponseEntity<String> testSave();
            MeetingDtl dtl = MeetingDtl.builder()
                    .meeting(mst)                    // @MapsId 로 MEETING_ID 공유
                    .file(file)
                    .meetingSummary(req.getDetail().getMeetingSummary())
                    .meetingStt(req.getDetail().getMeetingStt())
                    .build();
            dtl.setFrstRgstId("system"); // ✅ 등록자 ID 세팅

            meetingDtlRepository.save(dtl);
        }

        return mst.getMeetingId();
    }

    // 회의 조회 (단일 회의 정보 조회)
    @Transactional(readOnly = true)
    public MeetingMst getMeetingById(Long meetingId) {
        Optional<MeetingMst> meeting = meetingMstRepository.findById(meetingId);
        return meeting.orElse(null);
    }

    // 회의 수정
    @Transactional
    public boolean updateMeeting(Long meetingId, MeetingMstRequest request) {
        Optional<MeetingMst> existingMeeting = meetingMstRepository.findById(meetingId);
        if (existingMeeting.isPresent()) {
            MeetingMst meeting = existingMeeting.get();
            meeting.setMeetingName(request.getMeetingName());
            meeting.setMeetingState(request.getMeetingState());
            meeting.setMeetingDate(request.getMeetingDate());
            meeting.setMeetingDuration(request.getMeetingDuration());
            meetingMstRepository.save(meeting);
            return true;
        }
        return false;
    }

    // 회의 삭제
    @Transactional
    public boolean deleteMeeting(Long meetingId) {
        Optional<MeetingMst> meeting = meetingMstRepository.findById(meetingId);
        if (meeting.isPresent()) {
            meetingMstRepository.delete(meeting.get());
            return true;
        }
        return false;
    }

    // 음성 파일을 AI 서버로 보내고 텍스트를 받아 DB에 저장
    public Mono<ResponseEntity<String>> processRecordingAndStore(Long meetingId, MultipartFile file) {
        WebClient webClient = webClientBuilder.baseUrl("http://59.29.227.204:9292").build(); // WebClient 객체 생성
        return webClient.post() // WebClient 객체에서 .post() 사용
                .uri("/api/v1/transcribe") // AI 서버의 텍스트 변환 엔드포인트
                .contentType(MediaType.MULTIPART_FORM_DATA)  // 파일 업로드를 위한 Content-Type
                .bodyValue(file)  // MultipartFile을 전송
                .retrieve()
                .bodyToMono(String.class)  // 응답을 String으로 받음
                .flatMap(response -> {
                    // AI 서버에서 받은 텍스트를 DB에 저장
                    saveMeetingText(meetingId, response);
                    return Mono.just(ResponseEntity.ok("회의 녹음이 텍스트로 변환되어 저장되었습니다."));
                });
    }

    // 텍스트를 요약하고 액션 아이템을 받아 DB에 저장
    public Mono<ResponseEntity<String>> summarizeTextAndStore(Long meetingId, String text) {
        WebClient webClient = webClientBuilder.baseUrl("http://59.29.227.204:9292").build(); // WebClient 객체 생성
        return webClient.post() // WebClient 객체에서 .post() 사용
                .uri("/api/v1/summarize") // AI 서버의 요약 엔드포인트
                .contentType(MediaType.APPLICATION_JSON)  // JSON 요청 타입
                .bodyValue(new MeetingSummaryRequest(text))  // 요청 데이터 포맷
                .retrieve()
                .bodyToMono(SummaryAndActionItems.class)  // 응답을 SummaryAndActionItems 객체로 받음
                .flatMap(response -> {
                    // 받은 요약 및 액션 아이템을 DB에 저장
                    saveMeetingSummary(meetingId, response.getSummary());
                    saveActionItems(meetingId, response.getActionItems());
                    return Mono.just(ResponseEntity.ok("회의 텍스트가 요약되고 액션 아이템이 저장되었습니다."));
                });
    }


    // 회의 요약 저장
    private void saveMeetingSummary(Long meetingId, String summary) {
        // DB에 저장하는 동기적 작업
        MeetingDtl meetingDtl = meetingDtlRepository.findById(meetingId).orElseThrow();
        meetingDtl.setMeetingSummary(summary);
        meetingDtlRepository.save(meetingDtl);
    }

    // 액션 아이템 저장
    private void saveActionItems(Long meetingId, List<String> actionItems) {
        // 액션 아이템 저장 로직 구현
        // 예: 액션 아이템을 DB에 저장
    }

    // 회의 텍스트 저장
    private void saveMeetingText(Long meetingId, String meetingText) {
        // DB에 저장하는 동기적 작업
        MeetingDtl meetingDtl = meetingDtlRepository.findById(meetingId).orElseThrow();
        meetingDtl.setMeetingStt(meetingText);
        meetingDtlRepository.save(meetingDtl);
    }


}
