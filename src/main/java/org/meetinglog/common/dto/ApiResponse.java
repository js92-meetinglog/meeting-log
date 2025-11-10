package org.meetinglog.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    private T resultData;
    
    private ResultStatus resultStatus;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .resultData(data)
                .resultStatus(ResultStatus.success())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .resultData(data)
                .resultStatus(ResultStatus.success(message))
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .resultData(null)
                .resultStatus(ResultStatus.error(message))
                .build();
    }
    
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .resultData(null)
                .resultStatus(ResultStatus.error(code, message))
                .build();
    }
}