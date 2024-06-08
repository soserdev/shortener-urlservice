package io.jumper.urlservice.repository;

import io.jumper.urlservice.model.UrlData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
class UrlRepositoryTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.11").withExposedPorts(27017);

    @Autowired
    UrlRepository repository;

    @BeforeAll
    public static void setup() {
        mongo.start();
    }

    @AfterAll
    public static void tearDown() {
        mongo.stop();
    }

    @BeforeEach
    void setUp() {
        List<UrlData> urls = List.of(new UrlData("short-url", "long-url", "user-id"));
        repository.saveAll(urls);
    }


    @Test
    void connectionIsEstablished() {
        assertThat(mongo.isCreated()).isTrue();
        assertThat(mongo.isRunning()).isTrue();
    }

    @Test
    void shouldFindByShortUrl() {
        var url = repository.findByShortUrl("short-url");
        assertThat(url).isNotNull();
        assertThat(url).isPresent();
        assertThat(url.get().getShortUrl()).isEqualTo("short-url");
        assertThat(url.get().getLongUrl()).isEqualTo("long-url");
        assertThat(url.get().getUserid()).isEqualTo("user-id");
    }

}