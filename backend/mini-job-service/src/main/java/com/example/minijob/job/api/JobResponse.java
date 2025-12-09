// [FILE]
// - 목적: Job 응답 DTO
// - 주요 역할: Job 정보를 클라이언트에 전달하기 위한 응답 형식 정의
// - 관련 토이 버전: [BE-v0.2]
// - 권장 읽는 순서: 필드 → fromEntity() → getter
//
// [LEARN] 응답 DTO는 엔티티와 분리하여 API 스펙을 관리한다.
//         엔티티 변경이 API에 영향을 주지 않도록 한다.

package com.example.minijob.job.api;

import com.example.minijob.job.domain.Job;

import java.time.LocalDateTime;

// [Order 1] 응답 DTO
// - Job 엔티티를 API 응답 형식으로 변환
// - 토이 버전: [BE-v0.2]+
// [LEARN] 정적 팩토리 메서드(fromEntity)를 사용하여 변환 로직을 캡슐화한다.
public class JobResponse {

    private Long id;
    private String type;
    private String status;
    private String payload;
    private LocalDateTime createdAt;

    // 기본 생성자
    public JobResponse() {
    }

    public JobResponse(Long id, String type, String status, String payload, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    // [Order 2] 엔티티 → DTO 변환
    // [LEARN] 정적 팩토리 메서드로 변환 로직을 한 곳에 모은다.
    public static JobResponse fromEntity(Job job) {
        return new JobResponse(
                job.getId(),
                job.getType(),
                job.getStatus(),
                job.getPayload(),
                job.getCreatedAt()
        );
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getPayload() {
        return payload;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters (JSON 직렬화용)
    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
