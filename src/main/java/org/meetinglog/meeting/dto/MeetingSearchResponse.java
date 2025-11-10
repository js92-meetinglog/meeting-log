package org.meetinglog.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.meetinglog.elasticsearch.MeetingDocument;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingSearchResponse {
    
    private List<MeetingDocument> meetings;
    
    private long totalCount;
    
    private int currentPage;
    
    private int totalPages;
    
    private boolean hasNext;
    
    private boolean hasPrevious;
}