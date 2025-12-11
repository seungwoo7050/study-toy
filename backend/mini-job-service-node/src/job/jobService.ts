// [FILE]
// - 목적: Job 비즈니스 로직 집약
// - 주요 역할: 검증, 조회, 상태 변경, 스케줄러 훅 제공
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: 생성자 → CRUD → 헬퍼 → 스케줄 시뮬레이션
//
// [LEARN] 이 서비스 레이어는 컨트롤러/라우터에서 받은 요청을 검증하고 도메인 객체를 조작한다.
//         각 메서드는 명확한 역할을 가지고 있으며, 상태 전이는 도메인 객체의 메서드를 통해 수행된다.
import { HttpError } from '../common/errorHandler';
import { Job, JobStatus } from './job';
import { JobRepository } from './jobRepository';

export class JobService {
  constructor(private readonly repo: JobRepository) {}

  // [Order 1] Job 생성
  // 요청에서 전달받은 type과 payload를 검증하고 저장소에 위임하여 새 Job을 반환합니다.
  // - 입력 검증: type이 비어있으면 400 에러
  // - 성공 시: 생성된 Job 객체 반환
  createJob(type: string, payload?: string): Job {
    if (!type) {
      throw new HttpError(400, 'type is required');
    }
    return this.repo.save(type, payload);
  }

  // [Order 2] Job 목록 조회
  // 저장소에 저장된 모든 Job을 ID 순으로 반환합니다.
  listJobs(): Job[] {
    return this.repo.findAll();
  }

  // [Order 3] 단일 Job 조회
  // 주어진 ID로 저장소에서 Job을 찾습니다. 존재하지 않으면 404를 던집니다.
  getJob(id: string): Job {
    if (!id) {
      throw new HttpError(400, 'id is required');
    }

    const job = this.repo.findById(id);
    if (!job) {
      throw new HttpError(404, `Job ${id} not found`);
    }
    return job;
  }

  // [Order 4] Job 삭제
  // ID로 Job을 삭제합니다. 존재하지 않으면 404를 반환합니다.
  deleteJob(id: string): void {
    const removed = this.repo.delete(id);
    if (!removed) {
      throw new HttpError(404, `Job ${id} not found`);
    }
  }

  // [Order 4-1] Job 취소 (실패 상태로 표시)
  // - 비동기 작업 취소를 흉내 내기 위해 FAILED 상태를 사용한다.
  cancelJob(id: string): Job {
    const job = this.getJob(id);
    job.fail();
    return job;
  }

  // [Order 5] Job 상태 전이
  // 주어진 상태로 Job을 변경합니다. PENDING은 생성 시 자동으로 설정됩니다.
  // - IN_PROGRESS: start() 호출
  // - COMPLETED: complete() 호출
  // - FAILED: fail() 호출
  transition(id: string, status: JobStatus): Job {
    const job = this.getJob(id);
    if (status === 'IN_PROGRESS') job.start();
    if (status === 'COMPLETED') job.complete();
    if (status === 'FAILED') job.fail();
    return job;
  }

  // [Order 6] 다음 처리 대기 Job 찾기
  // scheduler가 호출하여 가장 먼저 생성된 PENDING Job을 찾습니다. 없으면 undefined.
  findNextPending(): Job | undefined {
    return this.listJobs().find((job) => job.status === 'PENDING');
  }
}