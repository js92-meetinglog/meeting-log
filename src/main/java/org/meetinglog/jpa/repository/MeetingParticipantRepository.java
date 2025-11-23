package org.meetinglog.jpa.repository;

import org.meetinglog.jpa.entity.MeetingParticipant;
import org.meetinglog.jpa.entity.MeetingParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipantRepository
        extends JpaRepository<MeetingParticipant, MeetingParticipantId> {
}
