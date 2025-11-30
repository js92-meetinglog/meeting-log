package org.meetinglog.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.meetinglog.entity.BaseEntity;

@Entity
@Table(name = "MEETING_PARTICIPANT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingParticipant extends BaseEntity {

    @EmbeddedId
    private MeetingParticipantId id;

    @Column(name = "USER_NAME")
    private String userName;

    @MapsId("meetingId") // 복합키 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEETING_ID")
    private MeetingMst meeting;
}
