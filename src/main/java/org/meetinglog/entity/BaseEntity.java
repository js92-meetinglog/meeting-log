package org.meetinglog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "FRST_RGST_ID")
    private String frstRgstId;

    // DB DEFAULT CURRENT_TIMESTAMP 사용 → 자바가 값을 넣지 않도록
    @Column(name = "FRST_RGST_DT", insertable = false, updatable = false)
    protected LocalDateTime frstRgstDt;

    @Column(name = "LAST_MDFY_ID")
    private String lastMdfyId;

    // DB ON UPDATE CURRENT_TIMESTAMP 사용
    @Column(name = "LAST_MDFY_DT", insertable = false, updatable = false)
    protected LocalDateTime lastMdfyDt;
}
