// [FILE]
// - 목적: Job REST 엔드포인트 정의
// - 주요 역할: CRUD + 오류 처리 연결
// - 관련 토이 버전: [Node-BE-v0.1]
//
// [LEARN] Express의 Router는 미들웨어 체인을 관리하는 경량 컨트롤러입니다.
//         각 핸들러에서 오류가 발생하면 next(err)를 통해 errorHandler로 전달됩니다.
import { Router } from 'express';
import { JobRepository } from '../../job/jobRepository';
import { JobService } from '../../job/jobService';

// 싱글턴 저장소/서비스를 만들어 라우터 전체에서 재사용합니다.
const repo = new JobRepository();
const service = new JobService(repo);

export const jobRouter = Router();

// [Order 1] Job 목록 조회
// GET /api/jobs -> 모든 Job을 JSON 배열로 반환
jobRouter.get('/', (_req, res) => {
  res.json(service.listJobs());
});

// [Order 2] Job 상세 조회
// GET /api/jobs/:id -> ID에 해당하는 Job을 반환. 존재하지 않으면 404
jobRouter.get('/:id', (req, res, next) => {
  try {
    res.json(service.getJob(req.params.id));
  } catch (err) {
    next(err);
  }
});

// [Order 3] Job 생성
// POST /api/jobs -> type과 payload로 새 Job을 생성하여 201 응답
jobRouter.post('/', (req, res, next) => {
  try {
    const { type, payload } = req.body;
    const job = service.createJob(type, payload);
    res.status(201).json(job);
  } catch (err) {
    next(err);
  }
});

// [Order 4] Job 삭제
// DELETE /api/jobs/:id -> Job을 삭제하고 204 상태를 반환. 존재하지 않으면 404
jobRouter.delete('/:id', (req, res, next) => {
  try {
    service.deleteJob(req.params.id);
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

// [Order 5] Job 취소
// POST /api/jobs/:id/cancel -> 진행 중인 Job을 FAILED 상태로 표시
jobRouter.post('/:id/cancel', (req, res, next) => {
  try {
    const cancelled = service.cancelJob(req.params.id);
    res.json(cancelled);
  } catch (err) {
    next(err);
  }
});

// Service 인스턴스를 scheduler에서 사용하기 위해 export
export const jobServiceSingleton = service;