package org.meetinglog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {

  @Column(name = "FRST_RGST_ID")
  @Default
  private String frstRgstId="SYSTEM";

  // DB DEFAULT CURRENT_TIMESTAMP 사용 → 자바가 값을 넣지 않도록
  @Column(name = "FRST_RGST_DT", insertable = false, updatable = false)
  protected LocalDateTime frstRgstDt;

  @Column(name = "LAST_MDFY_ID")
  @Default
  private String lastMdfyId="SYSTEM";

  // DB ON UPDATE CURRENT_TIMESTAMP 사용
  @Column(name = "LAST_MDFY_DT", insertable = false, updatable = false)
  protected LocalDateTime lastMdfyDt;

  @Column(name = "USE_YN", nullable = false, length = 1)
  @Default
  private String useYn = "Y";
}
