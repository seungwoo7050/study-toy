// [FILE]
// - 목적: 애플리케이션 실행 설정을 한 곳에서 관리
// - 주요 역할: 포트, JWT 시크릿 등 환경 변수를 안전하게 로드
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: 상수 정의 → 환경변수 로딩 → 설정 객체
//
// [LEARN] dotenv를 이용하면 .env 파일에 민감 정보를 두고 코드에서 안전하게 로드할 수 있다.
//         기본값을 명확히 두어 로컬 개발 편의성을 높이자.
import dotenv from 'dotenv';

dotenv.config();

export const appConfig = {
  port: parseInt(process.env.PORT ?? '8081', 10),
  jwtSecret: process.env.JWT_SECRET ?? 'dev-secret-change-me',
  jwtExpirationMs: parseInt(process.env.JWT_EXPIRATION ?? '86400000', 10),
};
