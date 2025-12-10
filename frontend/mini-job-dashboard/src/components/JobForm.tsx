// [FILE] JobForm 컴포넌트
// [LEARN] React 폼 컴포넌트로 Job 생성 기능 구현
// [Order 4] JobForm 컴포넌트 구현

import React, { useState } from 'react';
import { Job } from '../types/Job';

interface JobFormProps {
  onJobCreate: (job: Omit<Job, 'id' | 'createdAt' | 'updatedAt'>) => void;
}

const JobForm: React.FC<JobFormProps> = ({ onJobCreate }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  // [LEARN] handleSubmit는 폼 제출을 캡쳐하여 페이지 리로드를 방지하고,
  //         상위 컴포넌트가 정의한 `onJobCreate` 콜백으로 데이터를 전달합니다.
  //         클라이언트측 유효성 검사는 UX 향상에 유용하지만 서버 측 검증을 대체하지 않습니다.
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (title.trim() && description.trim()) {
      onJobCreate({
        title: title.trim(),
        description: description.trim(),
        status: 'PENDING',
      });
      setTitle('');
      setDescription('');
    }
  };

  return (
    <div className="job-form">
      <h2>새 Job 생성</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="title">제목:</label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Job 제목을 입력하세요"
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="description">설명:</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Job 설명을 입력하세요"
            rows={3}
            required
          />
        </div>
        <button type="submit" className="submit-btn">
          Job 생성
        </button>
      </form>
    </div>
  );
};

export default JobForm;