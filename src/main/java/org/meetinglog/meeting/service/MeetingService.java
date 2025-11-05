package org.meetinglog.meeting.service;

import lombok.RequiredArgsConstructor;
import org.meetinglog.file.entity.FileMst;
import org.meetinglog.file.repository.FileMstRepository;
import org.meetinglog.meeting.dto.MeetingDtlRequest;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.entity.MeetingDtl;
import org.meetinglog.meeting.entity.MeetingMst;
import org.meetinglog.meeting.repository.MeetingDtlRepository;
import org.meetinglog.meeting.repository.MeetingMstRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingMstRepository meetingMstRepository;
    private final MeetingDtlRepository meetingDtlRepository;
    private final FileMstRepository fileMstRepository;

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
import org.springframework.http.ResponseEntity;

public interface MeetingService {
    public ResponseEntity<String> testSave();
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

    // 회의 텍스트 저장 (회의 텍스트 변환 후 저장)
    @Transactional
    public void saveMeetingText(Long meetingId, String meetingText) {
        Optional<MeetingDtl> meetingDtl = meetingDtlRepository.findById(meetingId);
        if (meetingDtl.isPresent()) {
            MeetingDtl detail = meetingDtl.get();
            detail.setMeetingStt(meetingText); // 텍스트 저장
            meetingDtlRepository.save(detail);
        }
    }

    // 회의 요약 저장
    @Transactional
    public void saveMeetingSummary(Long meetingId, String summarizedText) {
        Optional<MeetingDtl> meetingDtl = meetingDtlRepository.findById(meetingId);
        if (meetingDtl.isPresent()) {
            MeetingDtl detail = meetingDtl.get();
            detail.setMeetingSummary(summarizedText); // 요약된 텍스트 저장
            meetingDtlRepository.save(detail);
        }
    }
}
