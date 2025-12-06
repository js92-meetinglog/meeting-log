package org.meetinglog.meeting.service;

import java.time.LocalDate;
import java.util.List;

import org.meetinglog.meeting.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface MeetingService {

    Long createMeeting(MeetingMstRequest req);

    List<MeetingListResponse> getMeetingList();

    void attachAudioToMeeting(Long meetingId, MultipartFile file);

    MeetingAudioResponse getMeeting(Long meetingId);

    void updateMeeting(Long meetingId, MeetingUpdateRequest request);


    MeetingSearchResponse searchMeetings(String keyword, List<String> participants,
                                              LocalDate startDate, LocalDate endDate,
                                              int page, int size);
}

