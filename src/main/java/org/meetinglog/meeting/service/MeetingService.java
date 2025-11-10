package org.meetinglog.meeting.service;

import org.meetinglog.meeting.dto.MeetingSearchRequest;
import org.meetinglog.meeting.dto.MeetingSearchResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface MeetingService {
    public ResponseEntity<String> testSave();
    
    public MeetingSearchResponse searchMeetings(String keyword, List<String> participants, 
                                              LocalDate startDate, LocalDate endDate, 
                                              int page, int size);
}
