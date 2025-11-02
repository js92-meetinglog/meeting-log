package org.meetinglog.meeting.repository;

import org.meetinglog.meeting.entity.MeetingDtl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDtlRepository extends JpaRepository<MeetingDtl, Long> {
}