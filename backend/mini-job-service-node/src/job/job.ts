// [FILE]
// - 목적: Job 도메인 모델 정의 (Node 버전)
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: 타입 → 생성자 → 상태 전이 메서드
//
// [LEARN] TypeScript의 class는 Java 객체와 유사하다. 필드를 public/private으로 선언해 안전하게 관리할 수 있다.
export type JobStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';

export class Job {
  id: string;
  type: string;
  status: JobStatus;
  payload?: string;
  createdAt: Date;
  updatedAt: Date;

  constructor(params: { id: string; type: string; payload?: string; status?: JobStatus; createdAt?: Date; updatedAt?: Date }) {
    this.id = params.id;
    this.type = params.type;
    this.payload = params.payload;
    this.status = params.status ?? 'PENDING';
    this.createdAt = params.createdAt ?? new Date();
    this.updatedAt = params.updatedAt ?? this.createdAt;
  }

  start() {
    this.status = 'IN_PROGRESS';
    this.touch();
  }

  complete() {
    this.status = 'COMPLETED';
    this.touch();
  }

  fail() {
    this.status = 'FAILED';
    this.touch();
  }

  private touch() {
    this.updatedAt = new Date();
  }
}
