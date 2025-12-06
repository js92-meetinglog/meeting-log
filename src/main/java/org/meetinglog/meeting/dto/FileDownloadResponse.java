package org.meetinglog.meeting.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@Builder
public class FileDownloadResponse {
  private Resource resource;
  private String contentType;
  private long contentLength;
}