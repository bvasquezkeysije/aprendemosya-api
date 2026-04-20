CREATE TABLE user_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(120),
    last_name VARCHAR(120),
    profile_image_url VARCHAR(500),
    bio TEXT,
    phone VARCHAR(30),
    birth_date DATE,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_profile_user
        FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_profile_user_id ON user_profile(user_id);
