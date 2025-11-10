
package org.meetinglog.elasticsearch;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Document(indexName = "meeting")
public class MeetingDocument {

    @Id
    @Schema(description = "ELS 고유 ID")
    private String id;

    @Field(type = FieldType.Text)
    @Schema(description = "회의록 아이디")
    private String meetingId;

    @Field(type = FieldType.Text, analyzer = "nori")
    @Schema(description = "회의록 타이틀")
    private String title;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "회의 날짜")
    private LocalDateTime meetingDate;

    @Field(type = FieldType.Keyword)
    @Schema(description = "회의 참석자")
    private List<String> participants;

    @Field(type = FieldType.Text, analyzer = "nori")
    @Schema(description = "회의 AI 요약")
    private String summary;

    @Field(type = FieldType.Text, analyzer = "nori")
    @Schema(description = "회의 전사본")
    private String transcription;
}
