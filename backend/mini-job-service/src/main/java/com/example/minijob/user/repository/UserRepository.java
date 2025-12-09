// [FILE]
// - 목적: User JPA Repository
// - 주요 역할: 사용자 데이터 접근
// - 관련 토이 버전: [BE-v0.6]
// - 권장 읽는 순서: 인터페이스 선언 확인
//
// [LEARN] email로 사용자를 조회하는 메서드를 정의한다.

package com.example.minijob.user.repository;

import com.example.minijob.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// [Order 1] User Repository
// - 토이 버전: [BE-v0.6]+
// [LEARN] findByEmail은 로그인 시 사용자를 조회할 때 사용한다.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
