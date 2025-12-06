package org.meetinglog.meeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.ai.AiTranscribeClient;
import org.meetinglog.ai.dto.TranscribeAndSummarizeResponse;
import org.meetinglog.elasticsearch.MeetingDocument;
import org.meetinglog.elasticsearch.MeetingDocumentRepository;
import org.meetinglog.jpa.entity.FileMst;
import org.meetinglog.jpa.entity.MeetingDtl;
import org.meetinglog.jpa.entity.MeetingMst;
import org.meetinglog.jpa.repository.FileMstRepository;
import org.meetinglog.jpa.repository.MeetingDtlRepository;
import org.meetinglog.jpa.repository.MeetingMstRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingAudioService {

    private final MeetingDtlRepository meetingDtlRepository;
    private final MeetingMstRepository meetingMstRepository;
    private final FileMstRepository fileMstRepository;
    private final MeetingDocumentRepository meetingDocumentRepository;

    private final FileStorageService fileStorageService;
    private final AiTranscribeClient aiClient;

    public void processMeetingAudio(Long meetingId) {

        MeetingDtl dtl = meetingDtlRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의 상세가 없습니다"));

        FileMst file = fileMstRepository.findById(dtl.getFile().getFileId())
                .orElseThrow(() -> new IllegalArgumentException("파일이 없습니다"));

        byte[] audioBytes = fileStorageService.loadFileBytes(file.getFilePath());
        String filename = "meeting-" + meetingId + "." + file.getFileExtension();

        TranscribeAndSummarizeResponse ai = aiClient.requestAi(audioBytes, filename);

        dtl.setMeetingStt(ai.transcript());
        dtl.setMeetingSummary(ai.summary());
        dtl.setKeyPoints(ai.keyPoints() != null ? String.join("\n", ai.keyPoints()) : "");
        dtl.setActionItems(ai.actionItems() != null ? String.join("\n", ai.actionItems()) : "");
        dtl.setLanguage(ai.language());

        meetingDtlRepository.save(dtl);

        MeetingMst mst = dtl.getMeeting();
        mst.setMeetingState("DONE");
        meetingMstRepository.save(mst);

        MeetingDocument document = MeetingDocument.builder()
            .id(String.valueOf(meetingId))
            .meetingId(String.valueOf(meetingId))
            .title(mst.getMeetingName())
            .meetingDate(mst.getMeetingDate())
            // .participants(...)
            .summary(dtl.getMeetingSummary())
            .transcription(dtl.getMeetingStt())
            .build();

        meetingDocumentRepository.save(document);

        log.info("AI 처리가 완료되었습니다 → meetingId={}", meetingId);
    }
}
