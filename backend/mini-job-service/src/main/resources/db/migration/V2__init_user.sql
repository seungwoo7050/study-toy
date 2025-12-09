-- [FILE]
-- - 목적: User 테이블 생성
-- - 주요 역할: Flyway 마이그레이션으로 users 테이블 생성
-- - 관련 토이 버전: [BE-v0.6]
--
-- [LEARN] 마이그레이션 파일은 한 번 적용되면 수정하지 않는다.
--         변경이 필요하면 새 마이그레이션 파일을 추가한다.

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 이메일 인덱스
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
