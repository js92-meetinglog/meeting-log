package org.meetinglog.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.meetinglog.entity.BaseEntity;

@Entity
@Table(name = "MEETING_DTL")
@Getter @Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @Lob
    @Column(name = "KEY_POINTS")
    private String keyPoints;

    @Lob
    @Column(name = "ACTION_ITEMS")
    private String actionItems;

    @Column(name = "LANGUAGE")
    private String language;

}
