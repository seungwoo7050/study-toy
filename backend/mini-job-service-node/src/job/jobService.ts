// [FILE]
// - 목적: Job 비즈니스 로직 집약
// - 주요 역할: 검증, 조회, 상태 변경, 스케줄러 훅 제공
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: 생성자 → CRUD → 헬퍼 → 스케줄 시뮬레이션
import { HttpError } from '../common/errorHandler';
import { Job, JobStatus } from './job';
import { JobRepository } from './jobRepository';

export class JobService {
  constructor(private readonly repo: JobRepository) {}

  createJob(type: string, payload?: string): Job {
    if (!type) {
      throw new HttpError(400, 'type is required');
    }
    return this.repo.save(type, payload);
  }

  listJobs(): Job[] {
    return this.repo.findAll();
  }

  getJob(id: number): Job {
    const job = this.repo.findById(id);
    if (!job) {
      throw new HttpError(404, `Job ${id} not found`);
    }
    return job;
  }

  deleteJob(id: number): void {
    const removed = this.repo.delete(id);
    if (!removed) {
      throw new HttpError(404, `Job ${id} not found`);
    }
  }

  transition(id: number, status: JobStatus): Job {
    const job = this.getJob(id);
    if (status === 'RUNNING') job.start();
    if (status === 'DONE') job.complete();
    if (status === 'FAILED') job.fail();
    return job;
  }

  findNextPending(): Job | undefined {
    return this.listJobs().find((job) => job.status === 'PENDING');
  }
}
