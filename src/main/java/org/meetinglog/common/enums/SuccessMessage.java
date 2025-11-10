package org.meetinglog.common.enums;

import lombok.Getter;

@Getter
public enum SuccessMessage {

    SUCCESS("성공"),
    SEARCH_COMPLETED("검색이 완료되었습니다."),
    DATA_SAVED("데이터가 저장되었습니다."),
    DATA_UPDATED("데이터가 수정되었습니다."),
    DATA_DELETED("데이터가 삭제되었습니다.");

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }
}
