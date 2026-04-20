package com.aprendemosya.aprendemosya_api.domain.auth.repository;

import com.aprendemosya.aprendemosya_api.domain.auth.dto.LoginResponse;
import com.aprendemosya.aprendemosya_api.domain.auth.dto.UserProfileResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<LoginResponse> login(String identifier, String password) {
        List<LoginResponse> results = jdbcTemplate.query(
                """
                        SELECT user_id, username, email, role, active, profile_image_url
                        FROM fn_auth_login(?, ?)
                        """,
                (rs, rowNum) -> new LoginResponse(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getBoolean("active"),
                        rs.getString("profile_image_url")
                ),
                identifier,
                password
        );

        return results.stream().findFirst();
    }

    public Optional<UserProfileResponse> findProfileByUserId(Long userId) {
        List<UserProfileResponse> results = jdbcTemplate.query(
                """
                        SELECT
                            user_id,
                            username,
                            email,
                            role,
                            active,
                            profile_image_url,
                            first_name,
                            last_name,
                            display_name
                        FROM fn_auth_get_user_profile(?)
                        """,
                (rs, rowNum) -> new UserProfileResponse(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getBoolean("active"),
                        rs.getString("profile_image_url"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("display_name")
                ),
                userId
        );

        return results.stream().findFirst();
    }
}
