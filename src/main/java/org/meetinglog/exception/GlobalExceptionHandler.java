package org.meetinglog.exception;

import lombok.extern.slf4j.Slf4j;
import org.meetinglog.common.dto.ApiResponse;
import org.meetinglog.common.exception.BusinessException;
import org.meetinglog.common.exception.SearchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        log.error("Business Exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(4000, e.getMessage()));
    }

    @ExceptionHandler(SearchException.class)
    public ResponseEntity<ApiResponse<Object>> handleSearchException(SearchException e) {
        log.error("Search Exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(4000, e.getMessage()));
    }

    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleOAuth2AuthenticationException(
            OAuth2AuthenticationException e, HttpServletRequest request) {
        log.error("OAuth2 Authentication Exception occurred on {}: {}", request.getRequestURI(), e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "error", "authentication_failed",
                    "message", "인증에 실패했습니다: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        log.error("Access Denied Exception occurred on {}: {}", request.getRequestURI(), e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                    "error", "access_denied",
                    "message", "접근이 거부되었습니다: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation Exception occurred on {}: {}", request.getRequestURI(), e.getMessage());

        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "validation_failed",
                    "message", "입력값 검증에 실패했습니다.",
                    "errors", errors,
                    "timestamp", System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("Type Mismatch Exception occurred on {}: {}", request.getRequestURI(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "type_mismatch",
                    "message", "잘못된 파라미터 타입입니다: " + e.getName(),
                    "timestamp", System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(
            EntityNotFoundException e, HttpServletRequest request) {
        log.error("Entity Not Found Exception occurred on {}: {}", request.getRequestURI(), e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "entity_not_found",
                    "message", "요청한 데이터를 찾을 수 없습니다: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException e, HttpServletRequest request) {
        log.error("Data Integrity Violation Exception occurred on {}: {}", request.getRequestURI(), e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "error", "data_integrity_violation",
                    "message", "데이터 무결성 위반입니다.",
                    "timestamp", System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        log.error("Illegal Argument Exception occurred on {}: {}", request.getRequestURI(), e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "invalid_argument",
                    "message", "잘못된 요청입니다: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException e, HttpServletRequest request) {
        log.error("Runtime Exception occurred on {}: {}", request.getRequestURI(), e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "internal_server_error",
                    "message", "서버 내부 오류가 발생했습니다.",
                    "timestamp", System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception e, HttpServletRequest request) {
        log.error("Unexpected Exception occurred on {}: {}", request.getRequestURI(), e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "unexpected_error",
                    "message", "예상치 못한 오류가 발생했습니다.",
                    "timestamp", System.currentTimeMillis()
                ));
    }
}
