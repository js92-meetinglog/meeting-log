package org.meetinglog.notion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.jpa.entity.MeetingDtl;
import org.meetinglog.jpa.entity.MeetingMst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotionServiceImpl implements NotionService {

    @Value("${notion.secret}")
    private String notionSecret;


    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    public void createMeetingPage(String parentPageId,
                                  MeetingMst mst,
                                  MeetingDtl dtl,
                                  List<String> participants) {

        try {
            Map<String, Object> requestBody = new HashMap<>();

            requestBody.put("parent", Map.of("page_id", parentPageId));
            requestBody.put("properties", buildProperties(mst));

            List<Map<String, Object>> children = new ArrayList<>();

            // --------------------------
            // 1) 기본 정보
            // --------------------------
            children.add(heading("회의 기본 정보"));
            children.add(paragraph("회의명: " + mst.getMeetingName()));
            children.add(paragraph("회의일자: " + mst.getMeetingDate()));
            children.add(paragraph("참석자: " + String.join(", ", participants)));
            children.add(paragraph("상태: " + mst.getMeetingState()));

            // --------------------------
            // 2) 요약
            // --------------------------
            if (dtl.getMeetingSummary() != null) {
                children.add(heading("요약"));
                children.add(paragraph(dtl.getMeetingSummary()));
            }

            // --------------------------
            // 3) 핵심 포인트
            // --------------------------
            if (dtl.getKeyPoints() != null) {
                children.add(heading("핵심 포인트"));

                List<String> points = Arrays.asList(dtl.getKeyPoints().split("\n"));
                for (String p : points) {
                    children.add(paragraph("- " + p.trim()));
                }
            }

            // --------------------------
            // 4) 액션 아이템
            // --------------------------
            if (dtl.getActionItems() != null) {
                children.add(heading("액션 아이템"));

                List<String> actions = Arrays.asList(dtl.getActionItems().split("\n"));
                for (String a : actions) {
                    children.add(paragraph("- " + a.trim()));
                }
            }

            // --------------------------
            // 5) 전체 회의록(STT) — 2000자 분할 + 토글 블록
            // --------------------------
            if (dtl.getMeetingStt() != null) {
                children.add(heading("전체 회의록 (STT)"));

                children.add(toggleBlock(
                        "▶ 클릭하여 전체 회의록 펼치기",
                        buildLargeTextBlocks(dtl.getMeetingStt(), 2000)
                ));
            }

            requestBody.put("children", children);

            // --------------------------
            // Notion API 호출
            // --------------------------
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + notionSecret);
            headers.set("Notion-Version", "2022-06-28");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity("https://api.notion.com/v1/pages", request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("회의록 Notion 업로드 성공");
            } else {
                log.error("Notion 업로드 실패: {}", response.getBody());
            }

        } catch (Exception e) {
            log.error("Notion API 오류: {}", e.getMessage());
        }
    }


    // --------------------------
    // ★ 유틸 함수 영역
    // --------------------------

    private Map<String, Object> buildProperties(MeetingMst mst) {
        return Map.of(
                "title", List.of(
                        Map.of(
                                "type", "text",
                                "text", Map.of("content", mst.getMeetingName())
                        )
                )
        );
    }

    private Map<String, Object> heading(String text) {
        return Map.of(
                "object", "block",
                "type", "heading_2",
                "heading_2", Map.of(
                        "rich_text", List.of(
                                Map.of(
                                        "type", "text",
                                        "text", Map.of("content", text)
                                )
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
                                Map.of(
                                        "type", "text",
                                        "text", Map.of("content", text)
                                )
                        )
                )
        );
    }

    private List<Map<String, Object>> buildLargeTextBlocks(String text, int limit) {

        List<Map<String, Object>> blocks = new ArrayList<>();

        int length = text.length();
        for (int start = 0; start < length; start += limit) {
            int end = Math.min(start + limit, length);
            String chunk = text.substring(start, end);

            blocks.add(paragraph(chunk));
        }

        return blocks;
    }

    private Map<String, Object> toggleBlock(String title, List<Map<String, Object>> children) {
        return Map.of(
                "object", "block",
                "type", "toggle",
                "toggle", Map.of(
                        "rich_text", List.of(
                                Map.of(
                                        "type", "text",
                                        "text", Map.of("content", title)
                                )
                        ),
                        "children", children
                )
        );
    }
}
