// [FILE]
// - 목적: Job 데이터 저장/조회 담당 (인메모리)
// - 주요 역할: 기본 CRUD 제공
// - 관련 토이 버전: [Node-BE-v0.1]
//
// [LEARN] 실제 DB가 없으므로 Map을 사용한다. persistence는 없지만 API 흐름을 학습하기 좋다.
import { Job } from './job';

export class JobRepository {
  private jobs = new Map<number, Job>();
  private nextId = 1;

  save(type: string, payload?: string): Job {
    const job = new Job({ id: this.nextId++, type, payload });
    this.jobs.set(job.id, job);
    return job;
  }

  findAll(): Job[] {
    return Array.from(this.jobs.values()).sort((a, b) => a.id - b.id);
  }

  findById(id: number): Job | undefined {
    return this.jobs.get(id);
  }

  delete(id: number): boolean {
    return this.jobs.delete(id);
  }
}
