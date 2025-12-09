// [FILE]
// - 목적: 메인 애플리케이션 컴포넌트
// - 주요 역할: Job 대시보드의 기본 레이아웃과 라우팅 구조 정의
// - 관련 토이 버전: [FE-F0.1], [FE-F0.2]+
// - 권장 읽는 순서: import → 컴포넌트 선언 → JSX 반환
//
// [LEARN] 함수형 컴포넌트와 JSX를 사용하여 UI를 선언적으로 구성한다.
//         이후 버전에서 JobList, JobForm 등의 컴포넌트를 추가할 예정이다.

import { useState } from 'react'
import './App.css'
import { Job } from './types/Job'
import JobList from './components/JobList'

// [Order 1] 메인 App 컴포넌트
// - 애플리케이션의 최상위 컴포넌트
// - 토이 버전: [FE-F0.1]+
// [LEARN] useState 훅을 사용하여 컴포넌트 상태를 관리한다.
//         이후 버전에서 Job 데이터를 상태로 관리할 예정이다.
function App() {
  // [Order 2] Dummy Job 데이터
  // - FE-F0.2: 더미 데이터로 Job 목록 표시
  // [LEARN] TypeScript 타입을 사용하여 데이터 구조를 명확히 정의
  const [jobs] = useState<Job[]>([
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
  ])

  return (
    <>
      <div>
        <h1>Mini Job Dashboard</h1>
        <JobList jobs={jobs} />
      </div>
    </>
  )
}

export default App