// [FILE] API 서비스
/// <reference types="vite/client" />
// [LEARN] fetch API를 사용하여 백엔드와 통신
// [Order 5] API 서비스 구현

import { Job } from '../types/Job';

type RawJob = {
  id: string;
  title: string;
  description?: string;
  status: string;
  createdAt: string;
  updatedAt: string;
};

// 기본값은 로컬 개발용: Vite 환경변수 `VITE_API_BASE_URL`을 사용하면 배포/테스트 환경에서 변경 가능합니다.
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL as string) ?? 'http://localhost:8080/api'; // 백엔드 서버 주소

export class JobApiService {
  // Job 목록 조회
  static async getJobs(): Promise<Job[]> {
    // [LEARN] API 호출은 실패할 수 있으므로 예외를 처리해야 합니다.
    //         클라이언트에서는 서버 부재 시 대체 값(더미 데이터)을 제공하여 개발 편의성을 높입니다.
    try {
      const response = await fetch(`${API_BASE_URL}/jobs`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const jobs = (await response.json()) as RawJob[];
      // 백엔드에서 받은 날짜 문자열을 Date 객체로 변환
      const allowedStatuses: Job['status'][] = ['PENDING', 'RUNNING', 'COMPLETED', 'FAILED'];
      return jobs.map((job: RawJob) => {
        const status = (job.status && allowedStatuses.includes(job.status as Job['status']))
          ? (job.status as Job['status'])
          : 'PENDING';
        return {
          id: job.id,
          title: job.title,
          description: job.description ?? '',
          status,
          createdAt: new Date(job.createdAt),
          updatedAt: new Date(job.updatedAt),
        } as Job;
      });
    } catch (error) {
      // 실패시 콘솔 대신 로깅/오류처리 로직으로 전환(개발 중에는 반환값으로 대체)
      // 백엔드가 없는 경우 더미 데이터 반환 (개발용)
      return this.getDummyJobs();
    }
  }

  // Job 생성
  static async createJob(jobData: Omit<Job, 'id' | 'createdAt' | 'updatedAt'>): Promise<Job> {
    // [LEARN] POST 요청은 서버의 유효성 검증을 통과해야 하므로 서버 에러를 고려하여
    //         에러 처리 전략을 정의해야 합니다. 학습용으로는 로컬 더미를 사용합니다.
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
      // 실패시 콘솔 대신 로깅/오류처리 로직으로 전환
      // 백엔드가 없는 경우 클라이언트에서 Job 생성 (개발용)
      return this.createDummyJob(jobData);
    }
  }

  // 개발용 더미 데이터
  private static getDummyJobs(): Job[] {
    // [LEARN] 실제 API가 없을 경우 빠르게 UI 동작을 확인하기 위한 로컬 더미 데이터를 제공합니다.
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
    // [LEARN] 클라이언트에서 임시로 ID를 생성하여 Job을 즉시 화면에 반영할 수 있도록 지원합니다.
    return {
      ...jobData,
      id: Date.now().toString(),
      createdAt: new Date(),
      updatedAt: new Date(),
    };
  }
}