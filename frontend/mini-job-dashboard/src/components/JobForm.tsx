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