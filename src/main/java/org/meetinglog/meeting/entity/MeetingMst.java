package org.meetinglog.meeting.entity;

import jakarta.persistence.*;
import lombok.*;
import org.meetinglog.entity.BaseEntity;

import java.time.LocalDateTime;
@Entity
@Table(name = "`MEETING_MST`")// ✅ 대문자 테이블 이름 강제 인식
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MeetingMst extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEETING_ID")
    private Long meetingId;

    @Column(name = "MEETING_NAME", nullable = false)
    private String meetingName;

    @Column(name = "MEETING_STATE")
    private String meetingState;

    @Column(name = "MEETING_DATE", nullable = false)
    private LocalDateTime meetingDate;

    @Column(name = "MEETING_DURATION")
    private Integer meetingDuration;

    @OneToOne(mappedBy = "meeting", fetch = FetchType.LAZY)
    private MeetingDtl detail;
}

