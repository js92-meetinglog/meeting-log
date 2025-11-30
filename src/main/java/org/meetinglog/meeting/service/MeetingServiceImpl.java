package org.meetinglog.meeting.service;

import lombok.AllArgsConstructor;
import org.meetinglog.elasticsearch.MeetingDocument;
import org.meetinglog.elasticsearch.MeetingLogRepository;
import org.meetinglog.jpa.entity.*;
import org.meetinglog.jpa.repository.FileMstRepository;
import org.meetinglog.jpa.repository.MeetingParticipantRepository;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.dto.MeetingParticipantRequest;
import org.meetinglog.meeting.dto.MeetingSearchResponse;
import org.meetinglog.jpa.repository.MeetingDtlRepository;
import org.meetinglog.jpa.repository.MeetingMstRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MeetingServiceImpl implements MeetingService {
    private MeetingMstRepository meetingMstRepository;
    private MeetingDtlRepository meetingDtlRepository;
    private FileMstRepository fileMstRepository;
    private MeetingLogRepository meetingLogRepository;
    private final FileStorageService fileStorageService;
    private final MeetingParticipantRepository meetingParticipantRepository;



    @Transactional
    @Override
    public Long createMeeting(MeetingMstRequest req) {

        // 1) 회의 마스터 저장
        MeetingMst mst = MeetingMst.builder()
                .meetingName(req.getMeetingName())
                .meetingState("PROCESSING")
                .meetingDate(LocalDateTime.now()) // 자동 생성
                .meetingDuration(req.getMeetingDuration())
                .build();
        mst.setFrstRgstId("system");

        mst = meetingMstRepository.save(mst);


        // 2) 참석자 저장
        if (req.getParticipants() != null) {
            for (MeetingParticipantRequest p : req.getParticipants()) {

                MeetingParticipantId id =
                        new MeetingParticipantId(mst.getMeetingId(), p.getUserId());

                MeetingParticipant participant = MeetingParticipant.builder()
                        .id(id)
                        .meeting(mst)
                        .userName(p.getUserName())
                        .build();

                participant.setFrstRgstId("system");
                meetingParticipantRepository.save(participant);
            }
        }

        // 3) 회의 상세 저장(옵션)
        MeetingDtl dtl = MeetingDtl.builder()
                .meeting(mst)
                .build();
        dtl.setFrstRgstId("system");
        meetingDtlRepository.save(dtl);

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

    @Transactional
    @Override
    public Long createMeetingFromFile(MultipartFile file) {

        // 1) 파일 저장
        FileMst savedFile = fileStorageService.saveFile(file);

        // 2) 회의 기본 생성
        MeetingMst mst = MeetingMst.builder()
                .meetingName("자동생성-" + System.currentTimeMillis())
                .meetingState("PROCESSING")
                .meetingDate(LocalDateTime.now())
                .build();

        mst.setFrstRgstId("system");
        mst = meetingMstRepository.save(mst);

        // 3) 회의 상세 생성
        MeetingDtl dtl = MeetingDtl.builder()
                .meeting(mst)
                .file(savedFile)
                .build();

        dtl.setFrstRgstId("system");
        meetingDtlRepository.save(dtl);

        return mst.getMeetingId();
    }

    @Transactional
    @Override
    public Long attachAudioToMeeting(Long meetingId, MultipartFile file) {

        // 1) 회의 조회
        MeetingMst mst = meetingMstRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의 없음"));

        // 2) 파일 저장
        FileMst savedFile = fileStorageService.saveFile(file);

        // 3) 회의 상세 조회 (없으면 새로 생성)
        MeetingDtl dtl = meetingDtlRepository.findById(meetingId)
                .orElse(MeetingDtl.builder()
                        .meeting(mst)
                        .build());

        dtl.setFile(savedFile);
        dtl.setFrstRgstId("system");

        meetingDtlRepository.save(dtl);

        return meetingId;
    }



}
