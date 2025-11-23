package org.meetinglog.meeting.service;

import java.time.LocalDate;
import java.util.List;
import org.meetinglog.meeting.dto.MeetingMstRequest;
import org.meetinglog.meeting.dto.MeetingSearchResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MeetingService {

    Long createMeeting(MeetingMstRequest req);

    Long createMeetingFromFile(MultipartFile file);

    Long attachAudioToMeeting(Long meetingId, MultipartFile file);

    MeetingSearchResponse searchMeetings(String keyword, List<String> participants,
                                              LocalDate startDate, LocalDate endDate,
                                              int page, int size);
}

