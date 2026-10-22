CREATE TABLE IF NOT EXISTS audit_records (
    id VARCHAR(64) PRIMARY KEY,
    sequence_number BIGINT NOT NULL,
    prev_hash VARCHAR(64) NOT NULL,
    hash VARCHAR(64) NOT NULL,
    agent VARCHAR(64) NOT NULL,
    tool VARCHAR(64) NOT NULL,
    verdict VARCHAR(16) NOT NULL,
    score INT NOT NULL,
    timestamp VARCHAR(35) NOT NULL,
    redacted_payload TEXT,
    reasons TEXT
);

CREATE INDEX IF NOT EXISTS idx_audit_seq ON audit_records (sequence_number);

CREATE TABLE IF NOT EXISTS approval_records (
    id VARCHAR(64) PRIMARY KEY,
    action_id VARCHAR(64) NOT NULL,
    agent VARCHAR(64) NOT NULL,
    tool VARCHAR(64) NOT NULL,
    recipient VARCHAR(255),
    score INT NOT NULL,
    reasons TEXT,
    redacted_payload TEXT,
    status VARCHAR(16) NOT NULL,
    reviewer VARCHAR(64),
    reviewed_at VARCHAR(35),
    created_at VARCHAR(35) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_approval_status ON approval_records (status);
