// [FILE]
// - 목적: 전역 예외 처리기
// - 주요 역할: 애플리케이션 전체에서 발생하는 예외를 일관되게 처리
// - 관련 토이 버전: [BE-v0.5]
// - 권장 읽는 순서: @ControllerAdvice → handleJobNotFound() → handleValidation() → handleGeneral()
//
// [LEARN] @ControllerAdvice로 전역 예외 처리기를 정의한다.
//         각 예외 타입별로 적절한 HTTP 상태 코드와 응답을 반환한다.

package com.example.minijob.common.exception;

import com.example.minijob.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

// [Order 1] 전역 예외 처리기
// - 모든 컨트롤러에서 발생하는 예외를 처리
// - 토이 버전: [BE-v0.5]+
// [LEARN] @ExceptionHandler로 특정 예외 타입을 처리하는 메서드를 정의한다.
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // [Order 2] Job 미존재 예외 처리
    // - 404 Not Found 반환
    // [LEARN] 비즈니스 예외에 대해 적절한 HTTP 상태 코드를 매핑한다.
    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJobNotFound(
            JobNotFoundException ex, HttpServletRequest request) {
        log.warn("Job not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // [Order 3] 검증 실패 예외 처리
    // - 400 Bad Request 반환
    // [LEARN] @Valid 검증 실패 시 발생하는 예외를 처리한다.
    //         각 필드의 에러 메시지를 수집하여 응답한다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // [Order 4] 일반 예외 처리
    // - 500 Internal Server Error 반환
    // [LEARN] 예상치 못한 예외에 대해 기본 처리를 제공한다.
    //         스택트레이스는 로그에만 남기고, 클라이언트에는 일반 메시지를 반환한다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: ", ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// [Reader Notes]
// ------------------------------------------------------------
// 이 파일에서 새로 등장한 개념:
// 1. @ControllerAdvice를 이용한 전역 예외 처리
// 2. @ExceptionHandler로 예외 타입별 처리
// 3. 검증 예외(MethodArgumentNotValidException) 처리
// 4. 로깅을 통한 에러 추적
//
// 다음에 보면 좋은 파일:
// - ErrorResponse.java: 에러 응답 DTO
// - JobNotFoundException.java: 커스텀 예외
