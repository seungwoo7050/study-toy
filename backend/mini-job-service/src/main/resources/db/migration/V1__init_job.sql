-- [FILE]
-- - 목적: Job 테이블 초기 생성
-- - 주요 역할: Flyway 마이그레이션으로 jobs 테이블 생성
-- - 관련 토이 버전: [BE-v0.4]
--
-- [LEARN] Flyway는 버전 기반 마이그레이션 도구다.
--         V{버전}__{설명}.sql 형식으로 파일을 만들면 자동 실행된다.

CREATE TABLE IF NOT EXISTS jobs (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payload TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성 (상태별 조회 최적화)
CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
CREATE INDEX IF NOT EXISTS idx_jobs_type ON jobs(type);
