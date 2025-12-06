package org.meetinglog.jpa.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MeetingParticipantId implements Serializable {

    private Long meetingId;
    private String unqId;
}
