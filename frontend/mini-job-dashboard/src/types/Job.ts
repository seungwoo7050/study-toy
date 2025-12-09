// [FILE] Job 타입 정의
// [LEARN] TypeScript 인터페이스로 Job 엔티티 구조 정의
// [Order 1] Job 인터페이스 정의

export interface Job {
  id: string;
  title: string;
  description: string;
  status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED';
  createdAt: Date;
  updatedAt: Date;
}