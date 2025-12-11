// [FILE]
// - 목적: API 전역 에러 처리
// - 주요 역할: 공통 응답 포맷 유지, 에러 메시지 로깅
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: 에러 타입 → 핸들러 구현
//
// [LEARN] Express는 마지막 미들웨어가 에러 핸들러 역할을 한다.
//         next(err)를 호출하거나 throw한 에러가 모여 표준화된 응답을 만든다.
import { Request, Response, NextFunction } from 'express';

export class HttpError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

export function errorHandler(err: Error, _req: Request, res: Response, _next: NextFunction) {
  const status = (err as HttpError).status ?? 500;
  const message = err.message || 'Unexpected error';

  // 간단한 콘솔 로그. 실서비스라면 로깅 프레임워크를 사용하세요.
  // eslint-disable-next-line no-console
  console.error(`[error] status=${status} message=${message}`);

  res.status(status).json({ status, message });
}
