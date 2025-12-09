// [FILE]
// - 목적: Job 스케줄러 (비동기 처리 시뮬레이션)
// - 주요 역할: PENDING 상태의 Job을 주기적으로 처리
// - 관련 토이 버전: [BE-v0.7]
// - 권장 읽는 순서: processJobs() → simulateJobProcessing()
//
// [LEARN] @Scheduled를 사용하여 주기적인 작업을 실행한다.
//         실제 시스템에서는 메시지 큐를 사용하지만, 여기서는 간단히 시뮬레이션한다.

package com.example.minijob.job.service;

import com.example.minijob.job.domain.Job;
import com.example.minijob.job.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

// [Order 1] Job 스케줄러
// - 토이 버전: [BE-v0.7]+
// [LEARN] @Scheduled(fixedRate)는 이전 실행 시작 시점부터 주기를 계산한다.
//         fixedDelay는 이전 실행 완료 시점부터 계산한다.
@Component
public class JobScheduler {

    private static final Logger log = LoggerFactory.getLogger(JobScheduler.class);
    private final JobRepository jobRepository;
    private final Random random = new Random();

    public JobScheduler(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // [Order 2] 주기적 Job 처리
    // - 10초마다 실행
    // [LEARN] PENDING 상태의 Job을 찾아서 처리한다.
    //         실제로는 외부 시스템 호출, 파일 처리 등을 수행한다.
    @Scheduled(fixedRate = 10000)  // 10초마다
    @Transactional
    public void processJobs() {
        List<Job> pendingJobs = jobRepository.findByStatus("PENDING");

        for (Job job : pendingJobs) {
            try {
                log.info("Processing job: {} (type: {})", job.getId(), job.getType());

                // 상태를 RUNNING으로 변경
                job.start();
                jobRepository.save(job);

                // Job 처리 시뮬레이션
                simulateJobProcessing(job);

                // 80% 확률로 성공
                if (random.nextInt(10) < 8) {
                    job.complete();
                    log.info("Job {} completed successfully", job.getId());
                } else {
                    job.fail();
                    log.warn("Job {} failed", job.getId());
                }

                jobRepository.save(job);

            } catch (Exception e) {
                log.error("Error processing job {}: {}", job.getId(), e.getMessage());
                job.fail();
                jobRepository.save(job);
            }
        }
    }

    // [Order 3] Job 처리 시뮬레이션
    // - 랜덤하게 1~3초 대기
    // [LEARN] 실제로는 이 부분에서 비디오 인코딩, 이메일 발송 등의 작업을 수행한다.
    private void simulateJobProcessing(Job job) {
        try {
            int processingTime = 1000 + random.nextInt(2000);  // 1~3초
            Thread.sleep(processingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
