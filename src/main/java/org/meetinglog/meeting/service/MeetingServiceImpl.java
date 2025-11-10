package org.meetinglog.meeting.service;

import org.meetinglog.elasticsearch.MeetingDocument;
import org.meetinglog.elasticsearch.MeetingLogRepository;
import org.meetinglog.jpa.entity.FileMst;
import org.meetinglog.jpa.repository.FileMstRepository;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.dto.MeetingSearchResponse;
import org.meetinglog.jpa.entity.MeetingDtl;
import org.meetinglog.jpa.entity.MeetingMst;
import org.meetinglog.jpa.repository.MeetingDtlRepository;
import org.meetinglog.jpa.repository.MeetingMstRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {
    @Autowired
    private MeetingMstRepository meetingMstRepository;
    @Autowired
    private MeetingDtlRepository meetingDtlRepository;
    @Autowired
    private FileMstRepository fileMstRepository;
    @Autowired
    private MeetingLogRepository meetingLogRepository;

    @Transactional
    @Override
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

    @Override
    public MeetingSearchResponse searchMeetings(String keyword, List<String> participants,
                                              LocalDate startDate, LocalDate endDate,
                                              int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "meetingDate"));

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }
        if (endDate != null) {
            endDateTime = endDate.atTime(LocalTime.MAX);
        }

        Page<MeetingDocument> result = searchByConditions(keyword, participants, startDateTime, endDateTime, pageable);

        return MeetingSearchResponse.builder()
                .meetings(result.getContent())
                .totalCount(result.getTotalElements())
                .currentPage(result.getNumber())
                .totalPages(result.getTotalPages())
                .hasNext(result.hasNext())
                .hasPrevious(result.hasPrevious())
                .build();
    }

    private Page<MeetingDocument> searchByConditions(String keyword, List<String> participants,
                                                   LocalDateTime startDate, LocalDateTime endDate,
                                                   Pageable pageable) {

        boolean hasKeyword = StringUtils.hasText(keyword);
        boolean hasParticipants = participants != null && !participants.isEmpty();
        boolean hasDateRange = startDate != null && endDate != null;

        if (hasKeyword && hasParticipants && hasDateRange) {
            return meetingLogRepository.findByKeywordAndParticipantsAndDateRange(keyword, participants, startDate, endDate, pageable);
        } else if (hasKeyword && hasParticipants) {
            return meetingLogRepository.findByKeywordAndParticipants(keyword, participants, pageable);
        } else if (hasKeyword && hasDateRange) {
            return meetingLogRepository.findByKeywordAndDateRange(keyword, startDate, endDate, pageable);
        } else if (hasParticipants && hasDateRange) {
            return meetingLogRepository.findByParticipantsAndDateRange(participants, startDate, endDate, pageable);
        } else if (hasKeyword) {
            return meetingLogRepository.findByKeyword(keyword, pageable);
        } else if (hasParticipants) {
            return meetingLogRepository.findByParticipants(participants, pageable);
        } else if (hasDateRange) {
            return meetingLogRepository.findByDateRange(startDate, endDate, pageable);
        } else {
            return meetingLogRepository.findAll(pageable);
        }
    }

}
