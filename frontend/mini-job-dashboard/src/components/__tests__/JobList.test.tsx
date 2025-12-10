import React from 'react';
import { render, screen } from '@testing-library/react';
import JobList from '../JobList';
import { Job } from '../../types/Job';

// Minimal learning-level Unit test for JobList component
// Purpose: JobList가 빈 목록/단일 Job 렌더링을 어떻게 처리하는지 확인하기 위한 간단한 테스트입니다.
// 학습 포인트:
//  - React 컴포넌트 렌더링
//  - 렌더된 DOM 내 텍스트 검증

const jobs: Job[] = [
  {
    id: '1',
    title: 'Test Job',
    description: 'This is a test job',
    status: 'PENDING',
    createdAt: new Date('2024-01-01T01:02:03Z'),
    updatedAt: new Date('2024-01-01T01:02:03Z'),
  },
];

test('renders job list header and a job card', () => {
  render(<JobList jobs={jobs} />);

  // Header
  expect(screen.getByRole('heading', { level: 2 })).toHaveTextContent('Job 목록');

  // Job card
  expect(screen.getByText('Test Job')).toBeInTheDocument();
  expect(screen.getByText('This is a test job')).toBeInTheDocument();
});
