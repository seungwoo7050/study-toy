import request from 'supertest';
import { describe, expect, it } from 'vitest';
import { createApp } from '../app';
import { jobServiceSingleton } from '../router/routes/jobRouter';
import { JobService } from '../job/jobService';
import { JobRepository } from '../job/jobRepository';

const app = createApp();

// 스케줄러 테스트는 인메모리 저장소 공유로 인해 상태가 섞이지 않도록 별도 인스턴스 사용
const isolatedService = new JobService(new JobRepository());

describe('health', () => {
  it('returns ok', async () => {
    const res = await request(app).get('/health');
    expect(res.status).toBe(200);
    expect(res.body).toEqual({ status: 'OK', message: 'mini-job-service-node is up' });
  });
});

describe('jobs API', () => {
  it('creates and lists jobs', async () => {
    const created = await request(app).post('/api/jobs').send({ type: 'demo', payload: 'data' });
    expect(created.status).toBe(201);
    expect(created.body.type).toBe('demo');
    expect(created.body.status).toBe('PENDING');
    expect(created.body.id).toBeDefined();

    const list = await request(app).get('/api/jobs');
    expect(list.status).toBe(200);
    expect(list.body.length).toBeGreaterThan(0);
  });

  it('cancels a job', async () => {
    const created = await request(app).post('/api/jobs').send({ type: 'cancel-demo' }).expect(201);
    const cancel = await request(app).post(`/api/jobs/${created.body.id}/cancel`).expect(200);

    expect(cancel.body.status).toBe('FAILED');
  });

  it('returns 404 for missing job', async () => {
    const res = await request(app).get('/api/jobs/9999');
    expect(res.status).toBe(404);
    expect(res.body.status).toBe(404);
  });
});

describe('auth API', () => {
  it('signs up and logs in', async () => {
    const email = `user_${Date.now()}@example.com`;
    const password = 'secret1234';

    const signup = await request(app).post('/api/auth/signup').send({ email, password });
    expect(signup.status).toBe(201);
    expect(signup.body.token).toBeDefined();

    const login = await request(app).post('/api/auth/login').send({ email, password });
    expect(login.status).toBe(200);
    expect(login.body.token).toBeDefined();
  });
});

describe('job service scheduler helper', () => {
  it('transitions jobs to running/done', async () => {
    const job = isolatedService.createJob('background');
    expect(job.status).toBe('PENDING');

    const running = isolatedService.transition(job.id, 'IN_PROGRESS');
    expect(running.status).toBe('IN_PROGRESS');

    const done = isolatedService.transition(job.id, 'COMPLETED');
    expect(done.status).toBe('COMPLETED');
  });
});
