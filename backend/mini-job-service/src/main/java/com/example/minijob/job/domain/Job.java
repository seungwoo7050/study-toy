// [FILE]
// - 목적: Job 도메인 모델 정의
// - 주요 역할: Job의 속성과 상태를 표현하는 엔티티 클래스
// - 관련 토이 버전: [BE-v0.2], [BE-v0.3]
// - 권장 읽는 순서: 필드 정의 → 생성자 → getter/setter
//
// [LEARN] 도메인 모델은 비즈니스 로직의 핵심이다.
//         JPA 엔티티로 전환할 때 @Entity, @Id 등의 어노테이션만 추가하면 된다.

package com.example.minijob.job.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// [Order 1] Job 엔티티
// - Job의 기본 속성: id, type, status, payload, createdAt
// - 토이 버전: [BE-v0.2] (POJO), [BE-v0.3] (JPA Entity)
// [LEARN] 처음에는 순수 자바 객체로 시작하고,
//         이후 JPA 어노테이션을 추가하여 영속성을 부여한다.
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // JPA를 위한 기본 생성자
    protected Job() {
    }

    // [Order 2] 비즈니스 생성자
    // - 새 Job을 생성할 때 사용
    // [LEARN] 생성자에서 기본값을 설정하여 객체 불변성을 높인다.
    public Job(String type, String payload) {
        this.type = type;
        this.status = "PENDING";
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }

    // 테스트/마이그레이션용 전체 필드 생성자
    public Job(Long id, String type, String status, String payload, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.payload = payload;
        this.createdAt = createdAt;
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

    // Setters (상태 변경용)
    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // [Order 3] 상태 전이 메서드
    // - Job의 상태를 변경하는 비즈니스 메서드
    // [LEARN] 상태 변경 로직을 도메인 객체 내부에 캡슐화한다.
    public void start() {
        this.status = "RUNNING";
    }

    public void complete() {
        this.status = "DONE";
    }

    public void fail() {
        this.status = "FAILED";
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
