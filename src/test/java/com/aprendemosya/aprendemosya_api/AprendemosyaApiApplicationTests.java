package com.aprendemosya.aprendemosya_api;

import com.aprendemosya.aprendemosya_api.domain.user.repository.AppUserRepository;
import com.aprendemosya.aprendemosya_api.domain.user.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		"GOOGLE_CLIENT_ID=test-google-client-id",
		"GOOGLE_CLIENT_SECRET=test-google-client-secret",
		"spring.autoconfigure.exclude=" +
				"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
				"org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration," +
				"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
				"org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
class AprendemosyaApiApplicationTests {

	@MockitoBean
	JdbcTemplate jdbcTemplate;

	@MockitoBean
	AppUserRepository appUserRepository;

	@MockitoBean
	UserProfileRepository userProfileRepository;

	@Test
	void contextLoads() {
	}

}
