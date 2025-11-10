package org.meetinglog.meeting.service;

import java.time.LocalDate;
import java.util.List;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.dto.MeetingSearchResponse;

public interface MeetingService {

    Long createMeeting(MeetingMstRequest req);

    MeetingSearchResponse searchMeetings(String keyword, List<String> participants,
                                              LocalDate startDate, LocalDate endDate,
                                              int page, int size);
}

