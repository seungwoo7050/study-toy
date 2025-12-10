// [FILE]
// - 목적: Job REST 엔드포인트 정의
// - 주요 역할: CRUD + 오류 처리 연결
// - 관련 토이 버전: [Node-BE-v0.1]
import { Router } from 'express';
import { JobRepository } from '../../job/jobRepository';
import { JobService } from '../../job/jobService';
import { HttpError } from '../../common/errorHandler';

const repo = new JobRepository();
const service = new JobService(repo);

export const jobRouter = Router();

jobRouter.get('/', (_req, res) => {
  res.json(service.listJobs());
});

jobRouter.get('/:id', (req, res, next) => {
  try {
    const id = Number(req.params.id);
    res.json(service.getJob(id));
  } catch (err) {
    next(err);
  }
});

jobRouter.post('/', (req, res, next) => {
  try {
    const { type, payload } = req.body;
    const job = service.createJob(type, payload);
    res.status(201).json(job);
  } catch (err) {
    next(err);
  }
});

jobRouter.delete('/:id', (req, res, next) => {
  try {
    const id = Number(req.params.id);
    if (Number.isNaN(id)) {
      throw new HttpError(400, 'id must be a number');
    }
    service.deleteJob(id);
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export const jobServiceSingleton = service;
