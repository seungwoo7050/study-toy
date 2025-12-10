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
        // [LEARN] 저장소의 save 메서드는 엔티티에 ID를 할당하고 영속화(저장)하는 책임이 있습니다.
        //         테스트 환경에서는 ID를 할당하는 로직을 구현하여 DB 없이도 동작하도록 합니다.
        if (job.getId() == null) {
            job.setId(sequence.getAndIncrement());
        }
        store.put(job.getId(), job);
        return job;
    }

    // [Order 3] ID로 Job 조회
    // [LEARN] Optional을 사용하여 null 처리를 명시적으로 한다.
    public Optional<Job> findById(Long id) {
        // [LEARN] Optional을 반환함으로써 null 대응을 호출자에게 위임합니다.
        //         서비스 계층에서는 orElseThrow 패턴을 통해 예외로 처리할 수 있습니다.
        return Optional.ofNullable(store.get(id));
    }

    // [Order 4] 전체 Job 목록 조회
    public List<Job> findAll() {
        // [LEARN] 저장소에서 값을 복사해 반환하여 외부에서 내부 컬렉션을 변경할 수 없도록 방지합니다.
        return new ArrayList<>(store.values());
    }

    // [Order 5] Job 삭제
    public void deleteById(Long id) {
        // [LEARN] 삭제 메서드는 존재 확인과 같은 추가 로직을 포함할 수 있으며,
        //         테스트 시에는 단순히 키를 제거하는 것으로 충분합니다.
        store.remove(id);
    }

    // [Order 6] 저장소 초기화 (테스트용)
    public void clear() {
        // [LEARN] 테스트 간의 간섭을 막기 위해 저장소 초기화 유틸리티를 제공합니다.
        store.clear();
        sequence.set(1);
    }
}
