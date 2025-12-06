package org.meetinglog.common.enums;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    SEARCH_ERROR("검색 중 오류가 발생했습니다.")
    ,INVALID_PAGE_NUMBER("페이지 번호는 0 이상이어야 합니다.")
    ,INVALID_PAGE_SIZE("페이지 크기는 1 이상 100 이하여야 합니다.")
    ,INVALID_DATE_RANGE("시작 날짜는 종료 날짜보다 빨라야 합니다.")
    ,INVALID_PARAMETER("잘못된 파라미터입니다.")
    ,INVALID_REQUEST("잘못된 요청입니다.")
    ,DATA_NOT_FOUND("요청한 데이터를 찾을 수 없습니다.")
    ,AUTHENTICATION_FAILED("인증에 실패했습니다.")
    ,ACCESS_DENIED("접근이 거부되었습니다.")
    ,INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.")
    ,VALIDATION_FAILED("입력값 검증에 실패했습니다.")
    ,TYPE_MISMATCH("잘못된 파라미터 타입입니다.")
    ,DATA_INTEGRITY_VIOLATION("데이터 무결성 위반입니다.")
    ,UNEXPECTED_ERROR("예상치 못한 오류가 발생했습니다.")
    ,LOGIN_FAILED("아이디 또는 비밀번호가 잘못되었습니다. ")
    ,SIGNUP_FAILED("회원가입에 실패했습니다.")
    ,DUPLICATE_ID("이미 존재하는 아이디입니다.")
    ,TOKEN_ERROR("정상적이지 않은 토큰입니다.")
    ,DB_PROCEDURES_ERROR("프로시저 오류가 발생했습니다.")
    ,FILE_DOWNLOAD_ERROR("파일 다운로드 중 오류가 발생했습니다.")
    ,UNKNOWN_FILE_ERROR("파일 정보를 찾을 수 없습니다.")
    ,UNKNOWN_MEETING_ERROR("회의 정보를 찾을 수 없습니다.")
    ,TOKEN_INFO_ERROR("토큰에 해당하는 유저를 찾을 수 없습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
