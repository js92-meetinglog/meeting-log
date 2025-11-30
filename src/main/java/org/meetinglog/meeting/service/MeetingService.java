package org.meetinglog.meeting.service;

import java.time.LocalDate;
import java.util.List;

import org.meetinglog.meeting.dto.MeetingAudioResponse;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.dto.MeetingSearchResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MeetingService {

    Long createMeeting(MeetingMstRequest req);

    Long attachAudioToMeeting(Long meetingId, MultipartFile file);

    MeetingAudioResponse getMeeting(Long meetingId);

    MeetingSearchResponse searchMeetings(String keyword, List<String> participants,
                                              LocalDate startDate, LocalDate endDate,
                                              int page, int size);
}

