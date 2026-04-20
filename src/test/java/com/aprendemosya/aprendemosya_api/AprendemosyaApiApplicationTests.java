package com.aprendemosya.aprendemosya_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=" +
				"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
				"org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration," +
				"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
				"org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
class AprendemosyaApiApplicationTests {

	@MockitoBean
	JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

}
