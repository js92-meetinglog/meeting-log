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
    public void pushMeetingToNotion(Long meetingId, String parentPageUrl) {

        MeetingMst mst = meetingMstRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의를 찾을 수 없습니다."));

        MeetingDtl dtl = meetingDtlRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의 상세정보를 찾을 수 없습니다."));

        List<String> participants = meetingParticipantRepository.findByIdMeetingId(meetingId)
                .stream()
                .map(MeetingParticipant::getUserName)
                .toList();

        // ⭐ URL에서 Page ID 추출
        String pageId = extractPageId(parentPageUrl);

        notionService.createMeetingPage(pageId, mst, dtl, participants);

        log.info("회의 Notion 업로드 완료 → meetingId={}", meetingId);
    }


    // -------------------------------------------------
// URL에서 pageId 추출하는 유틸 함수
// -------------------------------------------------
    private String extractPageId(String url) {
        if (url == null) {
            throw new IllegalArgumentException("Notion URL이 없습니다.");
        }

        // ? 뒤 제거
        String clean = url.split("\\?")[0];

        // 마지막 / 뒤 추출
        String lastPart = clean.substring(clean.lastIndexOf("/") + 1);

        // 1) 바로 pageId인 경우
        if (lastPart.matches("[a-fA-F0-9]{32}")) {
            return lastPart;
        }

        // 2) title-2bb4abcd... 처럼 섞여있는 경우
        if (lastPart.contains("-")) {
            String[] parts = lastPart.split("-");
            String candidate = parts[parts.length - 1];
            if (candidate.matches("[a-fA-F0-9]{32}")) {
                return candidate;
            }
        }

        throw new IllegalArgumentException("유효한 Notion 페이지 URL이 아닙니다.");
    }

}
