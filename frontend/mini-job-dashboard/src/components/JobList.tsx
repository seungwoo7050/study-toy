// [FILE] JobList 컴포넌트
// [LEARN] React 함수형 컴포넌트로 Job 목록 렌더링
// [Order 2] JobList 컴포넌트 구현

import React from 'react';
import { Job } from '../types/Job';

interface JobListProps {
  jobs: Job[];
}

const JobList: React.FC<JobListProps> = ({ jobs }) => {
  return (
    <div className="job-list">
      <h2>Job 목록</h2>
      {jobs.length === 0 ? (
        <p>등록된 Job이 없습니다.</p>
      ) : (
        <div className="job-grid">
          {jobs.map((job) => (
            <div key={job.id} className="job-card">
              <h3>{job.title}</h3>
              <p>{job.description}</p>
              <div className="job-status">
                <span className={`status status-${job.status.toLowerCase()}`}>
                  {job.status}
                </span>
              </div>
              <div className="job-dates">
                <small>생성: {job.createdAt.toLocaleString()}</small>
                <br />
                <small>수정: {job.updatedAt.toLocaleString()}</small>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default JobList;