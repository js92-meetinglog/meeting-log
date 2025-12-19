package org.meetinglog.meeting.service;

import org.meetinglog.meeting.dto.MeetingQaResponse;

public interface AiProcessService {
    void executeAsync(Long meetingId);

    MeetingQaResponse askQuestion(Long meetingId, String question);
}
