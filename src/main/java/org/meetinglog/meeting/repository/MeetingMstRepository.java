package org.meetinglog.meeting.repository;

import org.meetinglog.meeting.entity.MeetingMst;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingMstRepository extends JpaRepository<MeetingMst,Long> {
}
