package org.meetinglog.jpa.repository;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import org.meetinglog.jpa.entity.UserMst;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMstRepository extends JpaRepository<UserMst, Long> {

  Optional<UserMst> findByUserIdAndUseYn(String userId, String useYn);

  Optional<Object> findByUserId(String userId);
}
