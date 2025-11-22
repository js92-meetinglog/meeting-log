package org.meetinglog.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.meetinglog.entity.BaseEntity;
@Entity
@Table(name = "USER_MST")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserMst extends BaseEntity {

    @Id
    @Column(name = "UNQ_ID", nullable = false, length = 36)
    private String unqId;

    @Column(name = "USER_ID", nullable = false, unique = true, length = 100)
    private String userId;

    @Column(name = "USER_NM", nullable = false, length = 100)
    private String userNm;

    @Column(name = "TEL_NO", length = 50)
    private String telNo;
}