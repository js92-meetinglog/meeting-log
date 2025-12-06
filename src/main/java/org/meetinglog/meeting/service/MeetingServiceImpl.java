package org.meetinglog.meeting.service;

import lombok.AllArgsConstructor;
import org.meetinglog.elasticsearch.MeetingDocument;
import org.meetinglog.elasticsearch.MeetingLogRepository;
import org.meetinglog.jpa.entity.*;
import org.meetinglog.jpa.repository.FileMstRepository;
import org.meetinglog.jpa.repository.MeetingParticipantRepository;
import org.meetinglog.meeting.dto.*;
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

        MeetingMst mst = MeetingMst.builder()
                .meetingName(req.getMeetingName())
                .meetingState("PROCESSING")
                .meetingDate(LocalDateTime.now())
                .meetingDuration(0)
                .build();

        meetingMstRepository.save(mst);

        if (req.getParticipants() != null) {
            req.getParticipants().forEach(p -> {
                MeetingParticipant mp = MeetingParticipant.builder()
                        .id(new MeetingParticipantId(mst.getMeetingId(), p.getUserId()))
                        .meeting(mst)
                        .userName(p.getUserName())
                        .build();

                meetingParticipantRepository.save(mp);
            });
        }

        MeetingDtl dtl = MeetingDtl.builder()
                .meeting(mst)
                .build();

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
    public Long attachAudioToMeeting(Long meetingId, MultipartFile file) {

        var mst = meetingMstRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의 없음"));

        FileMst savedFile = fileStorageService.saveFile(file);

        MeetingDtl dtl = meetingDtlRepository.findById(meetingId)
                .orElse(MeetingDtl.builder().meeting(mst).build());

        dtl.setFile(savedFile);

        meetingDtlRepository.save(dtl);

        return meetingId;
    }

    @Override
    public MeetingAudioResponse getMeeting(Long meetingId) {

        MeetingDtl dtl = meetingDtlRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의 상세가 없습니다."));

        MeetingMst mst = dtl.getMeeting();

        return MeetingAudioResponse.builder()
                .meetingId(mst.getMeetingId())
                .transcript(dtl.getMeetingStt())
                .summary(dtl.getMeetingSummary())
                .keyPoints(dtl.getKeyPoints() != null ? List.of(dtl.getKeyPoints().split("\n")) : null)
                .actionItems(dtl.getActionItems() != null ? List.of(dtl.getActionItems().split("\n")) : null)
                .language(dtl.getLanguage())
                .status(mst.getMeetingState())
                .build();
    }

    @Override
    public List<MeetingListResponse> getMeetingList() {

        List<MeetingMst> list = meetingMstRepository.findAll(
                Sort.by(Sort.Direction.DESC, "meetingDate")
        );

        return list.stream().map(m -> {

            // 회의 상세 가져오기
            MeetingDtl dtl = meetingDtlRepository.findById(m.getMeetingId()).orElse(null);

            // 참여자 수 계산
            int participantCount =
                    meetingParticipantRepository.findByIdMeetingId(m.getMeetingId()).size();

            return MeetingListResponse.builder()
                    .meetingId(m.getMeetingId())
                    .meetingName(m.getMeetingName())
                    .meetingState(m.getMeetingState())
                    .meetingDate(m.getMeetingDate().toLocalDate().toString())
                    .meetingTime(m.getMeetingDate().toLocalTime().toString())
                    .meetingDuration(m.getMeetingDuration())
                    .participantCount(participantCount)
                    .meetingSummary(dtl != null ? dtl.getMeetingSummary() : null)
                    .build();

        }).toList();
    }




}
