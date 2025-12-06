package org.meetinglog.common.context;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CurrentUserData {
  private String unqId;
  private String userId;
  private String UserNm;
}