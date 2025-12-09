// [FILE] API 서비스
/// <reference types="vite/client" />
// [LEARN] fetch API를 사용하여 백엔드와 통신
// [Order 5] API 서비스 구현

import { Job } from '../types/Job';

// 기본값은 로컬 개발용: Vite 환경변수 `VITE_API_BASE_URL`을 사용하면 배포/테스트 환경에서 변경 가능합니다.
const API_BASE_URL = ((import.meta.env as any).VITE_API_BASE_URL as string) ?? 'http://localhost:8080/api'; // 백엔드 서버 주소

export class JobApiService {
  // Job 목록 조회
  static async getJobs(): Promise<Job[]> {
    try {
      const response = await fetch(`${API_BASE_URL}/jobs`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const jobs = await response.json();
      // 백엔드에서 받은 날짜 문자열을 Date 객체로 변환
      return jobs.map((job: any) => ({
        ...job,
        createdAt: new Date(job.createdAt),
        updatedAt: new Date(job.updatedAt),
      }));
    } catch (error) {
      console.error('Failed to fetch jobs:', error);
      // 백엔드가 없는 경우 더미 데이터 반환 (개발용)
      return this.getDummyJobs();
    }
  }

  // Job 생성
  static async createJob(jobData: Omit<Job, 'id' | 'createdAt' | 'updatedAt'>): Promise<Job> {
    try {
      const response = await fetch(`${API_BASE_URL}/jobs`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(jobData),
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const job = await response.json();
      return {
        ...job,
        createdAt: new Date(job.createdAt),
        updatedAt: new Date(job.updatedAt),
      };
    } catch (error) {
      console.error('Failed to create job:', error);
      // 백엔드가 없는 경우 클라이언트에서 Job 생성 (개발용)
      return this.createDummyJob(jobData);
    }
  }

  // 개발용 더미 데이터
  private static getDummyJobs(): Job[] {
    return [
      {
        id: '1',
        title: '데이터 백업 작업',
        description: '매일 밤 2시에 데이터베이스 백업을 수행합니다.',
        status: 'COMPLETED',
        createdAt: new Date('2024-01-15T10:00:00'),
        updatedAt: new Date('2024-01-15T10:30:00'),
      },
      {
        id: '2',
        title: '로그 정리 작업',
        description: '30일 이상 된 로그 파일을 정리합니다.',
        status: 'RUNNING',
        createdAt: new Date('2024-01-15T14:00:00'),
        updatedAt: new Date('2024-01-15T14:15:00'),
      },
      {
        id: '3',
        title: '이메일 알림 작업',
        description: '사용자에게 주간 리포트를 이메일로 발송합니다.',
        status: 'PENDING',
        createdAt: new Date('2024-01-15T16:00:00'),
        updatedAt: new Date('2024-01-15T16:00:00'),
      },
    ];
  }

  // 개발용 더미 Job 생성
  private static createDummyJob(jobData: Omit<Job, 'id' | 'createdAt' | 'updatedAt'>): Job {
    return {
      ...jobData,
      id: Date.now().toString(),
      createdAt: new Date(),
      updatedAt: new Date(),
    };
  }
}