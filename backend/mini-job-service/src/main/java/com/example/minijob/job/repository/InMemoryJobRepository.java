// [FILE]
// - 목적: 메모리 기반 Job 저장소 (BE-v0.2용)
// - 주요 역할: Job 객체를 메모리에 저장/조회/삭제
// - 관련 토이 버전: [BE-v0.2]
// - 권장 읽는 순서: 필드 → save() → findById() → findAll() → delete()
//
// [LEARN] DB 없이 Repository 패턴을 먼저 이해한다.
//         나중에 JPA Repository로 교체할 때 인터페이스만 바꾸면 된다.

package com.example.minijob.job.repository;

import com.example.minijob.job.domain.Job;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// [Order 1] 인메모리 저장소
// - ConcurrentHashMap을 사용하여 스레드 안전한 저장소 구현
// - 토이 버전: [BE-v0.2]
// [LEARN] Repository 패턴은 데이터 접근 로직을 캡슐화한다.
//         이후 JPA로 전환해도 서비스 레이어 코드는 변경하지 않아도 된다.
@Repository
public class InMemoryJobRepository {

    private final Map<Long, Job> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    // [Order 2] Job 저장
    // - 새 Job에 ID를 부여하고 저장
    // [LEARN] AtomicLong으로 스레드 안전하게 시퀀스를 생성한다.
    public Job save(Job job) {
        if (job.getId() == null) {
            job.setId(sequence.getAndIncrement());
        }
        store.put(job.getId(), job);
        return job;
    }

    // [Order 3] ID로 Job 조회
    // [LEARN] Optional을 사용하여 null 처리를 명시적으로 한다.
    public Optional<Job> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    // [Order 4] 전체 Job 목록 조회
    public List<Job> findAll() {
        return new ArrayList<>(store.values());
    }

    // [Order 5] Job 삭제
    public void deleteById(Long id) {
        store.remove(id);
    }

    // [Order 6] 저장소 초기화 (테스트용)
    public void clear() {
        store.clear();
        sequence.set(1);
    }
}
