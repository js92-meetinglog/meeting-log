package org.meetinglog.notion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.jpa.entity.MeetingDtl;
import org.meetinglog.jpa.entity.MeetingMst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotionService {

    @Value("${notion.secret}")
    private String notionSecret;

    @Value("${notion.parent-page-id}")
    private String parentPageId;

    private final RestTemplate restTemplate = new RestTemplate();


    public void createMeetingPage(MeetingMst mst, MeetingDtl dtl, List<String> participants) {

        String url = "https://api.notion.com/v1/pages";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + notionSecret);
        headers.set("Content-Type", "application/json");
        headers.set("Notion-Version", "2022-06-28");

        Map<String, Object> payload = buildPagePayload(mst, dtl, participants);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("Notion 페이지 생성 완료 → {}", mst.getMeetingName());
        } catch (Exception e) {
            log.error("Notion API 오류: {}", e.getMessage());
        }
    }


    private Map<String, Object> buildPagePayload(MeetingMst mst, MeetingDtl dtl, List<String> participants) {

        return Map.of(
                "parent", Map.of("page_id", parentPageId),
                "properties", Map.of(
                        "title", List.of(
                                Map.of("type", "text", "text", Map.of("content", mst.getMeetingName()))
                        )
                ),
                "children", List.of(
                        heading("회의 기본 정보"),
                        paragraph("회의명: " + mst.getMeetingName()),
                        paragraph("회의일자: " + mst.getMeetingDate().toString()),
                        paragraph("참석자: " + String.join(", ", participants)),
                        paragraph("상태: " + mst.getMeetingState()),

                        heading("요약"),
                        paragraph(defaultString(dtl.getMeetingSummary())),

                        heading("핵심 포인트"),
                        paragraph(defaultString(dtl.getKeyPoints())),

                        heading("액션 아이템"),
                        paragraph(defaultString(dtl.getActionItems())),

                        heading("전체 회의록(STT)"),
                        paragraph(defaultString(dtl.getMeetingStt()))
                )
        );
    }


    private Map<String, Object> heading(String text) {
        return Map.of(
                "object", "block",
                "type", "heading_2",
                "heading_2", Map.of(
                        "rich_text", List.of(
                                Map.of("type", "text", "text", Map.of("content", text))
                        )
                )
        );
    }

    private Map<String, Object> paragraph(String text) {
        return Map.of(
                "object", "block",
                "type", "paragraph",
                "paragraph", Map.of(
                        "rich_text", List.of(
                                Map.of("type", "text", "text", Map.of("content", text))
                        )
                )
        );
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
