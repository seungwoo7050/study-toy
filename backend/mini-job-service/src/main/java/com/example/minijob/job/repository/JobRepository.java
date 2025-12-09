// [FILE]
// - 목적: JPA 기반 Job 저장소 인터페이스 (BE-v0.3+)
// - 주요 역할: Spring Data JPA를 통한 Job CRUD 자동 구현
// - 관련 토이 버전: [BE-v0.3]+
// - 권장 읽는 순서: 인터페이스 선언 확인
//
// [LEARN] JpaRepository를 상속하면 기본 CRUD 메서드가 자동으로 제공된다.
//         메서드 이름 규칙(findByXxx)으로 쿼리를 자동 생성할 수 있다.

package com.example.minijob.job.repository;

import com.example.minijob.job.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// [Order 1] JPA Repository 인터페이스
// - JpaRepository<Entity, ID타입>을 상속
// - 토이 버전: [BE-v0.3]+
// [LEARN] 인터페이스만 정의하면 Spring Data JPA가 구현체를 자동 생성한다.
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // [Order 2] 커스텀 쿼리 메서드
    // - 상태별 Job 조회
    // [LEARN] 메서드 이름으로 쿼리가 자동 생성된다: SELECT * FROM jobs WHERE status = ?
    List<Job> findByStatus(String status);

    // 타입별 Job 조회
    List<Job> findByType(String type);
}
