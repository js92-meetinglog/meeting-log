
package org.meetinglog.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingDocumentRepository extends ElasticsearchRepository<MeetingDocument, String> {

    List<MeetingDocument> findByTitleContainingOrTranscriptionContaining(String title, String transcription);

    List<MeetingDocument> findByParticipantsIn(List<String> participants);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^2\", \"summary^1.5\", \"transcription\"]}}]}}")
    Page<MeetingDocument> findByKeyword(String keyword, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^2\", \"summary^1.5\", \"transcription\"]}}], \"filter\": [{\"range\": {\"meetingDate\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}}")
    Page<MeetingDocument> findByKeywordAndDateRange(String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^2\", \"summary^1.5\", \"transcription\"]}}, {\"terms\": {\"participants\": ?1}}]}}")
    Page<MeetingDocument> findByKeywordAndParticipants(String keyword, List<String> participants, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^2\", \"summary^1.5\", \"transcription\"]}}, {\"terms\": {\"participants\": ?1}}], \"filter\": [{\"range\": {\"meetingDate\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}}")
    Page<MeetingDocument> findByKeywordAndParticipantsAndDateRange(String keyword, List<String> participants, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("{\"bool\": {\"filter\": [{\"range\": {\"meetingDate\": {\"gte\": \"?0\", \"lte\": \"?1\"}}}]}}")
    Page<MeetingDocument> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"terms\": {\"participants\": ?0}}]}}")
    Page<MeetingDocument> findByParticipants(List<String> participants, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"terms\": {\"participants\": ?0}}], \"filter\": [{\"range\": {\"meetingDate\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}}")
    Page<MeetingDocument> findByParticipantsAndDateRange(List<String> participants, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
