// [FILE]
// - 목적: Job 비즈니스 로직 처리
// - 주요 역할: Job 생성, 조회, 상태 변경 등의 서비스 레이어 로직
// - 관련 토이 버전: [BE-v0.2], [BE-v0.3]+
// - 권장 읽는 순서: 생성자 → createJob() → getJob() → getAllJobs() → deleteJob()
//
// [LEARN] 서비스 레이어는 비즈니스 로직을 담당한다.
//         컨트롤러와 저장소 사이에서 트랜잭션 경계를 관리한다.

package com.example.minijob.job.service;

import com.example.minijob.job.domain.Job;
import com.example.minijob.job.repository.JobRepository;
import com.example.minijob.common.exception.JobNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// [Order 1] Job 서비스
// - @Service로 스프링 빈 등록
// - @Transactional로 트랜잭션 관리
// - 토이 버전: [BE-v0.2]+
// [LEARN] 서비스 레이어에서 비즈니스 규칙을 적용하고,
//         여러 저장소를 조합한 복잡한 작업을 처리한다.
@Service
@Transactional(readOnly = true)
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // [Order 2] Job 생성
    // - 새 Job을 생성하고 저장
    // - 토이 버전: [BE-v0.2]+
    // [LEARN] @Transactional을 메서드에 붙이면 쓰기 작업이 가능해진다.
    //         클래스 레벨의 readOnly=true를 오버라이드한다.
    @Transactional
    public Job createJob(String type, String payload) {
        Job job = new Job(type, payload);
        return jobRepository.save(job);
    }

    // [Order 3] ID로 Job 조회
    // - 없으면 예외 발생
    // [LEARN] Optional을 사용하여 null 처리를 명시적으로 한다.
    public Job getJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
    }

    // [Order 4] 전체 Job 목록 조회
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // [Order 5] 상태별 Job 조회
    public List<Job> getJobsByStatus(String status) {
        return jobRepository.findByStatus(status);
    }

    // [Order 6] Job 삭제
    // - 토이 버전: [BE-v0.2]+
    @Transactional
    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new JobNotFoundException("Job not found with id: " + id);
        }
        jobRepository.deleteById(id);
    }

    // [Order 7] Job 상태 변경
    // - 토이 버전: [BE-v0.7]에서 스케줄러가 사용
    @Transactional
    public Job updateJobStatus(Long id, String status) {
        Job job = getJob(id);
        job.setStatus(status);
        return jobRepository.save(job);
    }
}
