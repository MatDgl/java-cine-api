package com.example.java_cine_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class JavaCineApiApplicationTests {

	@Test
	void contextLoads() {
		// Test que le contexte Spring se charge correctement
	}

}
