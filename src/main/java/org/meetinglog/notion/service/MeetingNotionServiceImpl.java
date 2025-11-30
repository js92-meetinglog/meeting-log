package org.meetinglog.notion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.jpa.entity.MeetingDtl;
import org.meetinglog.jpa.entity.MeetingMst;
import org.meetinglog.jpa.entity.MeetingParticipant;
import org.meetinglog.jpa.repository.MeetingDtlRepository;
import org.meetinglog.jpa.repository.MeetingMstRepository;
import org.meetinglog.jpa.repository.MeetingParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingNotionServiceImpl implements MeetingNotionService {

    private final MeetingMstRepository meetingMstRepository;
    private final MeetingDtlRepository meetingDtlRepository;

    // 🚀 JPA 기반 repository만 사용
    private final MeetingParticipantRepository meetingParticipantRepository;

    private final NotionService notionService;

    @Override
    @Transactional(readOnly = true)
    public void pushMeetingToNotion(Long meetingId) {

        MeetingMst mst = meetingMstRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의를 찾을 수 없습니다."));

        MeetingDtl dtl = meetingDtlRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의 상세정보를 찾을 수 없습니다."));

        List<String> participants = meetingParticipantRepository.findByIdMeetingId(meetingId)
                .stream()
                .map(MeetingParticipant::getUserName)
                .toList();

        notionService.createMeetingPage(mst, dtl, participants);

        log.info("회의 Notion 업로드 완료 → meetingId={}", meetingId);
    }
}
