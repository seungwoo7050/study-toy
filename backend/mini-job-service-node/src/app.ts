// [FILE]
// - 목적: Express 앱 생성 및 공통 미들웨어/라우터 등록
// - 주요 역할: JSON 파서, CORS 설정, 라우터 묶기, 에러 핸들러 제공
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: 미들웨어 → 라우터 등록 → 에러 핸들러
//
// [LEARN] Express 앱을 함수로 분리하면 테스트 시 서버를 직접 띄우지 않고
//         supertest와 함께 깔끔하게 검증할 수 있다.
import express from 'express';
import cors from 'cors';
import { router } from './router';
import { errorHandler } from './common/errorHandler';

export function createApp() {
  const app = express();

  app.use(cors({ origin: ['http://localhost:5173', 'http://localhost:3000', 'http://localhost:8081'] }));
  app.use(express.json());
  app.use(express.urlencoded({ extended: true }));

  app.use(router);
  app.use(errorHandler);

  return app;
}
