//
//package org.meetinglog.elasticsearch;
//
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//import java.util.List;
//
//public interface MeetingLogSearchRepository extends ElasticsearchRepository<MeetingDocument, String> {
//
//    List<MeetingDocument> findByTitleContainingOrTranscriptionContaining(String title, String transcription);
//
//    List<MeetingDocument> findByParticipantsIn(List<String> participants);
//}
