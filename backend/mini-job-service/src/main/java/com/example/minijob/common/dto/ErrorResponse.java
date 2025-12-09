// [FILE]
// - 목적: 에러 응답 DTO
// - 주요 역할: API 에러 응답 형식 정의
// - 관련 토이 버전: [BE-v0.5]
// - 권장 읽는 순서: 필드 → 생성자 → getter
//
// [LEARN] 일관된 에러 응답 형식을 정의하면 클라이언트가 에러를 처리하기 쉽다.

package com.example.minijob.common.dto;

import java.time.LocalDateTime;

// [Order 1] 에러 응답 DTO
// - 모든 에러에 대해 일관된 형식으로 응답
// - 토이 버전: [BE-v0.5]+
// [LEARN] status, message, timestamp 등 기본 정보를 포함한다.
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
