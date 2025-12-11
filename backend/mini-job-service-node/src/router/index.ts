// [FILE]
// - 목적: 도메인별 라우터를 하나로 묶어 Express 앱에 제공
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: health → jobs → auth
//
// [LEARN] Router.use()를 이용해 하위 라우터를 접속 경로에 등록합니다.
//         prefix가 다른 라우터를 분리하면 코드가 모듈화되고 유지보수가 쉬워집니다.
import { Router } from 'express';
import { healthRouter } from './routes/healthRouter';
import { jobRouter } from './routes/jobRouter';
import { authRouter } from './routes/authRouter';

export const router = Router();
router.use('/health', healthRouter);
router.use('/api/jobs', jobRouter);
router.use('/api/auth', authRouter);