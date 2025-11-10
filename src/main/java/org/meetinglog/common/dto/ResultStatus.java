package org.meetinglog.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.meetinglog.common.enums.ErrorMessage;
import org.meetinglog.common.enums.SuccessMessage;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultStatus {

    private int code;

    private String message;

    public static ResultStatus success() {
        return ResultStatus.builder()
                .code(2000)
                .message(SuccessMessage.SUCCESS.getMessage())
                .build();
    }

    public static ResultStatus success(String message) {
        return ResultStatus.builder()
                .code(2000)
                .message(message)
                .build();
    }

    public static ResultStatus success(SuccessMessage successMessage) {
        return ResultStatus.builder()
                .code(2000)
                .message(successMessage.getMessage())
                .build();
    }

    public static ResultStatus error(String message) {
        return ResultStatus.builder()
                .code(4000)
                .message(message)
                .build();
    }

    public static ResultStatus error(ErrorMessage errorMessage) {
        return ResultStatus.builder()
                .code(4000)
                .message(errorMessage.getMessage())
                .build();
    }

    public static ResultStatus error(int code, String message) {
        return ResultStatus.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ResultStatus error(int code, ErrorMessage errorMessage) {
        return ResultStatus.builder()
                .code(code)
                .message(errorMessage.getMessage())
                .build();
    }
}
