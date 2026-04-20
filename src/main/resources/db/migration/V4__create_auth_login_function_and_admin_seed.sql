CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS role VARCHAR(30) NOT NULL DEFAULT 'USER';

UPDATE app_user
SET role = 'USER'
WHERE role IS NULL;

INSERT INTO app_user (
    username,
    email,
    password_hash,
    role,
    active
)
SELECT
    'admin',
    'admin@aprendemosya.com',
    crypt('76636255', gen_salt('bf')),
    'ADMIN',
    TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM app_user
    WHERE username = 'admin' OR email = 'admin@aprendemosya.com'
);

CREATE OR REPLACE FUNCTION fn_auth_login(
    p_identifier VARCHAR,
    p_password VARCHAR
)
RETURNS TABLE (
    user_id BIGINT,
    username VARCHAR,
    email VARCHAR,
    role VARCHAR,
    active BOOLEAN,
    profile_image_url VARCHAR
)
LANGUAGE plpgsql
STABLE
AS $$
BEGIN
    RETURN QUERY
    SELECT
        u.id,
        u.username,
        u.email,
        u.role,
        u.active,
        up.profile_image_url
    FROM app_user u
    LEFT JOIN user_profile up ON up.user_id = u.id
    WHERE
        (u.username = p_identifier OR u.email = p_identifier)
        AND u.active = TRUE
        AND u.password_hash = crypt(p_password, u.password_hash)
    LIMIT 1;
END;
$$;
