package org.meetinglog.meeting.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MeetingAudioRequest {
    private MultipartFile audioFile;   // 필드명은 audio_file과 매핑됨
}

