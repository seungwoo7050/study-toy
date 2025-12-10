// [FILE]
// - 목적: 도메인별 라우터를 하나로 묶어 Express 앱에 제공
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: health → jobs → auth
import { Router } from 'express';
import { healthRouter } from './routes/healthRouter';
import { jobRouter } from './routes/jobRouter';
import { authRouter } from './routes/authRouter';

export const router = Router();
router.use('/health', healthRouter);
router.use('/api/jobs', jobRouter);
router.use('/api/auth', authRouter);
