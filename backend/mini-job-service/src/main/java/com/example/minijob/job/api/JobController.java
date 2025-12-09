// [FILE]
// - 목적: Job 관련 HTTP 엔드포인트 집합
// - 주요 역할: /api/jobs 요청을 서비스 레이어로 위임하고, 응답 DTO로 매핑
// - 관련 토이 버전: [BE-v0.2], [BE-v0.3]+
// - 권장 읽는 순서: constructor → createJob() → getJob() → listJobs() → deleteJob()
//
// [LEARN] Spring MVC에서 @RestController, @RequestMapping, DTO 변환 패턴을 익힌다.
//         서비스와 컨트롤러를 분리하여 "웹 계층"을 구성하는 기본 구조를 학습한다.

package com.example.minijob.job.api;

import com.example.minijob.job.domain.Job;
import com.example.minijob.job.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// [Order 1] Job 컨트롤러
// - REST API 엔드포인트 정의
// - 토이 버전: [BE-v0.2]+
// [LEARN] @RestController는 @Controller + @ResponseBody의 조합이다.
//         @RequestMapping으로 기본 경로를 설정한다.
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // [Order 2] Job 생성 엔드포인트
    // - 클라이언트에서 들어온 DTO를 서비스로 전달하고, 생성된 Job을 응답 DTO로 변환
    // - 토이 버전: [BE-v0.2]+
    // [LEARN] 컨트롤러는 비즈니스 로직을 직접 구현하지 않고,
    //         DTO 변환과 HTTP 상태코드 처리에 집중한다.
    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
        Job job = jobService.createJob(request.getType(), request.getPayload());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JobResponse.fromEntity(job));
    }

    // [Order 3] 단일 Job 조회
    // - 경로 변수로 ID를 받아 Job 조회
    // [LEARN] @PathVariable로 URL 경로의 변수를 메서드 파라미터로 받는다.
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id) {
        Job job = jobService.getJob(id);
        return ResponseEntity.ok(JobResponse.fromEntity(job));
    }

    // [Order 4] Job 목록 조회
    // - 선택적으로 status 파라미터로 필터링
    // [LEARN] @RequestParam으로 쿼리 파라미터를 받는다.
    //         required=false로 선택적 파라미터를 정의한다.
    @GetMapping
    public ResponseEntity<List<JobResponse>> listJobs(
            @RequestParam(required = false) String status) {
        List<Job> jobs;
        if (status != null && !status.isEmpty()) {
            jobs = jobService.getJobsByStatus(status);
        } else {
            jobs = jobService.getAllJobs();
        }

        List<JobResponse> response = jobs.stream()
                .map(JobResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // [Order 5] Job 삭제
    // - 토이 버전: [BE-v0.2]+
    // [LEARN] DELETE 요청은 204 No Content를 반환하는 것이 일반적이다.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}

// [Reader Notes]
// ------------------------------------------------------------
// 이 파일에서 새로 등장한 개념:
// 1. @RestController와 @RequestMapping을 이용한 REST API 정의
// 2. @Valid를 이용한 요청 데이터 검증
// 3. ResponseEntity를 이용한 HTTP 상태 코드 제어
// 4. Stream API를 이용한 DTO 변환
//
// 다음에 보면 좋은 파일:
// - JobService.java: 비즈니스 로직 처리
// - GlobalExceptionHandler.java: 예외 처리 (BE-v0.5)
