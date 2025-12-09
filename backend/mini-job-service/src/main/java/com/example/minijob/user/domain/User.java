// [FILE]
// - 목적: User 도메인 모델 정의
// - 주요 역할: 사용자 계정 정보를 표현하는 엔티티
// - 관련 토이 버전: [BE-v0.6]
// - 권장 읽는 순서: 필드 → 생성자 → getter
//
// [LEARN] 인증/인가에 필요한 사용자 정보를 저장한다.
//         비밀번호는 BCrypt로 해시하여 저장한다.

package com.example.minijob.user.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// [Order 1] User 엔티티
// - 사용자 인증 정보 저장
// - 토이 버전: [BE-v0.6]+
// [LEARN] email은 unique 제약조건으로 중복을 방지한다.
//         role 필드로 권한을 관리한다.
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // JPA를 위한 기본 생성자
    protected User() {
    }

    // [Order 2] 비즈니스 생성자
    // [LEARN] 생성 시점에 기본값을 설정한다.
    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
