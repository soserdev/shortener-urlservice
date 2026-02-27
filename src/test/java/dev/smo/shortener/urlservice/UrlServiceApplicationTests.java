package dev.smo.shortener.urlservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.mongodb.MongoDBContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UrlServiceApplicationTests {

	@Autowired
	MongoDBContainer mongo;

	@Test
	void contextLoads() {
		assertThat(mongo.isCreated()).isTrue();
		assertThat(mongo.isRunning()).isTrue();
	}

}
