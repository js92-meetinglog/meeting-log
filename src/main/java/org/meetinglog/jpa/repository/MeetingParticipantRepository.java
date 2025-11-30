package org.meetinglog.jpa.repository;

import org.meetinglog.jpa.entity.MeetingParticipant;
import org.meetinglog.jpa.entity.MeetingParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingParticipantRepository
        extends JpaRepository<MeetingParticipant, MeetingParticipantId> {
    List<MeetingParticipant> findByIdMeetingId(Long meetingId);

}
