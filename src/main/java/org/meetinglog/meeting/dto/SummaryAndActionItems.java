package org.meetinglog.meeting.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SummaryAndActionItems {
    private String summary;
    private List<String> actionItems;
}
