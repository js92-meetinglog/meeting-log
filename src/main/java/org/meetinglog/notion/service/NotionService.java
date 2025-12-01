package org.meetinglog.notion.service;

import org.meetinglog.jpa.entity.MeetingDtl;
import org.meetinglog.jpa.entity.MeetingMst;

import java.util.List;

public interface NotionService {
    void createMeetingPage(String parentPageId, MeetingMst mst, MeetingDtl dtl, List<String> participants);
}
