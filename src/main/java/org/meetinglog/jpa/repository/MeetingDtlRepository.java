package org.meetinglog.jpa.repository;

import org.meetinglog.jpa.entity.MeetingDtl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDtlRepository extends JpaRepository<MeetingDtl, Long> {
}
