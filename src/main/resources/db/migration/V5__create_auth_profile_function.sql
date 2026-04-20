CREATE OR REPLACE FUNCTION fn_auth_get_user_profile(
    p_user_id BIGINT
)
RETURNS TABLE (
    user_id BIGINT,
    username VARCHAR,
    email VARCHAR,
    role VARCHAR,
    active BOOLEAN,
    profile_image_url VARCHAR,
    first_name VARCHAR,
    last_name VARCHAR,
    display_name VARCHAR
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
        up.profile_image_url,
        up.first_name,
        up.last_name,
        COALESCE(
            NULLIF(TRIM(CONCAT(COALESCE(up.first_name, ''), ' ', COALESCE(up.last_name, ''))), ''),
            u.username
        )::VARCHAR
    FROM app_user u
    LEFT JOIN user_profile up ON up.user_id = u.id
    WHERE u.id = p_user_id
      AND u.active = TRUE
    LIMIT 1;
END;
$$;
