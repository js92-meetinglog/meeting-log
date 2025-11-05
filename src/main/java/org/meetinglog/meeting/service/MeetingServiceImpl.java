package org.meetinglog.meeting.service;

import lombok.RequiredArgsConstructor;
import org.meetinglog.elasticsearch.MeetingDocument;
import org.meetinglog.elasticsearch.MeetingLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingLogRepository meetingLogRepository;

    @Override
    public ResponseEntity<String> testSave() {

        MeetingDocument testDoc = MeetingDocument.builder()
                .meetingId("202511011111")
                .title("회의록 테스트")
                .meetingDate(LocalDateTime.now())
                .participants(List.of("이유리", "김철수"))
                .summary("오늘 회의는 '미팅로그' 프로젝트의 1차 스프린트 중간 점검을 주제로 진행되었습니다. 핵심 안건은 Elasticsearch를 활용한 검색 기능 구현 상태였습니다. 논의 결과, 'meeting' 인덱스의 매핑 설계를 확정했으며, 한국어 검색 품질 향상을 위해 'nori' 형태소 분석기를 도입하기로 결정했습니다. 또한, 다음 주까지 검색 API의 프로토타입 개발을 완료하고, 클라이언트 팀과 연동 테스트를 시작하기로 합의했습니다.")
                .transcription("이유리: 안녕하세요, 오늘 미팅로그 프로젝트 스프린트 중간 점검 회의 시작하겠습니다. 김철수 님, 검색 기능 파트는 어떻게 진행되고 있나요?\n" +
                        "\n" +
                        "김철수: 네, 현재 Spring Boot 애플리케이션과 Elasticsearch 컨테이너 연동은 완료했습니다. 어제 말씀드렸던 'meeting' 인덱스 매핑 문제도 해결했습니다.\n" +
                        "\n" +
                        "이유리: 아, 다행이네요. 그럼 'nori' 분석기 적용도 확인된 건가요?\n" +
                        "\n" +
                        "김철수: 네. 방금 Kibana에서 'GET /meeting/_mapping' 쿼리로 확인해보니 'title'이랑 'transcription' 필드에 'analyzer: nori'가 정상적으로 적용된 것을 확인했습니다.\n" +
                        "\n" +
                        "이유리: 좋습니다. 그럼 이제 실제 데이터 저장 테스트가 필요하겠네요. 오늘 중으로 제가 더미 데이터 만들어서 API로 저장 테스트 한번 해보겠습니다.\n" +
                        "\n" +
                        "김철수: 알겠습니다. 그럼 저는 검색 API 엔드포인트 설계서 바로 정리해서 공유 드릴게요. 'participants' 필드로 필터링하는 기능도 추가해야겠죠?\n" +
                        "\n" +
                        "이유리: 네, 그거 꼭 필요합니다. 참석자 이름으로 검색하는 기능은 기획서의 핵심 요구사항이었으니까요. 그럼 다음 주 월요일까지 API 프로토타입 완료로 일정 잡겠습니다.\n" +
                        "\n" +
                        "김철수: 네, 문제없습니다.")
                .build();

        meetingLogRepository.save(testDoc);

        return null;
    }
}
