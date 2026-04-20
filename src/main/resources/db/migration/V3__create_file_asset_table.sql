CREATE TABLE file_asset (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    category VARCHAR(50) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL UNIQUE,
    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_file_asset_user
        FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE SET NULL
);

CREATE INDEX idx_file_asset_user_id ON file_asset(user_id);
CREATE INDEX idx_file_asset_category ON file_asset(category);
