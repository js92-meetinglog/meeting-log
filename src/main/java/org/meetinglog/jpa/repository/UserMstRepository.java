package org.meetinglog.jpa.repository;


import java.util.Optional;
import org.meetinglog.jpa.entity.UserMst;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMstRepository extends JpaRepository<UserMst, Long> {

  Optional<UserMst> findByUserIdAndUseYn(String userId, String useYn);
}
