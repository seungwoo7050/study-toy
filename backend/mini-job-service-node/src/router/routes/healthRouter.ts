// [FILE]
// - 목적: 헬스체크 엔드포인트 정의
// - 주요 역할: 서비스가 살아있는지 빠르게 확인
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: router 선언 → 핸들러
import { Router } from 'express';

export const healthRouter = Router();
healthRouter.get('/', (_req, res) => {
  res.json({ status: 'ok' });
});
