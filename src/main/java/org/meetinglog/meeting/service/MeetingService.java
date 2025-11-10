package org.meetinglog.meeting.service;

import lombok.RequiredArgsConstructor;
import org.meetinglog.file.entity.FileMst;
import org.meetinglog.file.repository.FileMstRepository;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.entity.MeetingDtl;
import org.meetinglog.meeting.entity.MeetingMst;
import org.meetinglog.meeting.repository.MeetingDtlRepository;
import org.meetinglog.meeting.repository.MeetingMstRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingMstRepository meetingMstRepository;
    private final MeetingDtlRepository meetingDtlRepository;
    private final FileMstRepository fileMstRepository;

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
    public MeetingSearchResponse searchMeetings(String keyword, List<String> participants,
                                              LocalDate startDate, LocalDate endDate,
                                              int page, int size);
}

