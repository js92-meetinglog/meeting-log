package org.meetinglog.jpa.repository;

import org.meetinglog.jpa.entity.MeetingMst;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingMstRepository extends JpaRepository<MeetingMst,Long> {
}
