package org.meetinglog.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.meetinglog.entity.BaseEntity;

@Entity
@Table(name = "MEETING_DTL")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MeetingDtl extends BaseEntity {

    @Id
    @Column(name = "MEETING_ID")
    private Long meetingId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "MEETING_ID")
    private MeetingMst meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileMst file;

    @Lob
    @Column(name = "MEETING_SUMMARY")
    private String meetingSummary;

    @Lob
    @Column(name = "MEETING_STT")
    private String meetingStt;
}
