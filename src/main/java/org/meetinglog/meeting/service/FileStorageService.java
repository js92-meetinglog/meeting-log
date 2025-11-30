package org.meetinglog.meeting.service;

import org.meetinglog.jpa.entity.FileMst;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    FileMst saveFile(MultipartFile file);
}
