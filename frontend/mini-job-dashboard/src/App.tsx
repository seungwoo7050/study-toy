// [FILE]
// - 목적: 메인 애플리케이션 컴포넌트
// - 주요 역할: Job 대시보드의 기본 레이아웃과 라우팅 구조 정의
// - 관련 토이 버전: [FE-F0.1], [FE-F0.2], [FE-F0.3]+
// - 권장 읽는 순서: import → 컴포넌트 선언 → JSX 반환
//
// [LEARN] 함수형 컴포넌트와 JSX를 사용하여 UI를 선언적으로 구성한다.
//         이후 버전에서 JobList, JobForm 등의 컴포넌트를 추가할 예정이다.

import { useState, useEffect } from 'react'
import './App.css'
import { Job } from './types/Job'
import JobList from './components/JobList'
import JobForm from './components/JobForm'
import { JobApiService } from './services/JobApiService'

// [Order 1] 메인 App 컴포넌트
// - 애플리케이션의 최상위 컴포넌트
// - 토이 버전: [FE-F0.1]+
// [LEARN] useState 훅을 사용하여 컴포넌트 상태를 관리한다.
//         이후 버전에서 Job 데이터를 상태로 관리할 예정이다.
function App() {
  // [Order 2] Job 상태 관리
  // - FE-F0.2+: Job 목록 상태 관리
  // - FE-F0.4: API에서 데이터를 불러와 상태 관리
  // [LEARN] useState로 Job 배열을 관리하고, 로딩 및 에러 상태도 추가
  const [jobs, setJobs] = useState<Job[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // [Order 3] Job 목록 로드
  // - FE-F0.4: 컴포넌트 마운트 시 API에서 Job 목록을 불러옴
  // - FE-F0.5: Job 상태 폴링 기능 추가
  // [LEARN] useEffect를 사용하여 컴포넌트 마운트 시 데이터를 불러오고, 주기적으로 업데이트
  useEffect(() => {
    const loadJobs = async () => {
      try {
        setLoading(true)
        const jobData = await JobApiService.getJobs()
        setJobs(jobData)
        setError(null)
      } catch (err) {
        setError('Job 목록을 불러오는데 실패했습니다.')
        console.error('Failed to load jobs:', err)
      } finally {
        setLoading(false)
      }
    }

    loadJobs()

    // [Order 5] Job 상태 폴링
    // - FE-F0.5: 10초마다 Job 상태를 확인하여 업데이트
    // [LEARN] setInterval을 사용하여 주기적으로 Job 상태를 폴링
    const pollInterval = setInterval(async () => {
      try {
        const jobData = await JobApiService.getJobs()
        setJobs(jobData)
        setError(null)
      } catch (err) {
        // 폴링 중 에러는 콘솔에만 기록 (UI에 표시하지 않음)
        console.error('Failed to poll jobs:', err)
      }
    }, 10000) // 10초마다 폴링

    // 컴포넌트 언마운트 시 인터벌 정리
    return () => clearInterval(pollInterval)
  }, [])

  // [Order 4] Job 생성 핸들러
  // - FE-F0.3: JobForm에서 새로운 Job을 생성할 때 호출
  // - FE-F0.4: API를 통해 Job을 생성
  // [LEARN] 새로운 Job을 생성하여 jobs 상태에 추가
  const handleJobCreate = async (newJobData: Omit<Job, 'id' | 'createdAt' | 'updatedAt'>) => {
    try {
      const newJob = await JobApiService.createJob(newJobData)
      setJobs(prevJobs => [...prevJobs, newJob])
      setError(null)
    } catch (err) {
      setError('Job 생성에 실패했습니다.')
      console.error('Failed to create job:', err)
    }
  }

  return (
    <>
      <div>
        <h1>Mini Job Dashboard</h1>

        <div className="status-indicator">
          <span className="polling-status">🔄 실시간 상태 모니터링 중 (10초 간격)</span>
        </div>

        {error && (
          <div className="error-message">
            <p>⚠️ {error}</p>
          </div>
        )}

        <JobForm onJobCreate={handleJobCreate} />

        {loading ? (
          <div className="loading">
            <p>Job 목록을 불러오는 중...</p>
          </div>
        ) : (
          <JobList jobs={jobs} />
        )}
      </div>
    </>
  )
}

export default App