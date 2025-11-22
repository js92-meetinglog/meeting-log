package org.meetinglog.jpa.repository;

import java.util.Optional;
import org.meetinglog.jpa.entity.UserAuthInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthInfoRepository extends JpaRepository<UserAuthInfo, Long> {

  Optional<UserAuthInfo> findByUnqIdAndLoginTypeAndUseYn(String unqId, String loginType, String useYn);
}
