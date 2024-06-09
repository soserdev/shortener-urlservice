package io.jumper.urlservice.repository;

import io.jumper.urlservice.model.UrlData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest
class UrlRepositoryTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.11").withExposedPorts(27017);

    @Autowired
    UrlRepository repository;

    @Test
    void connectionIsEstablished() {
        assertThat(mongo.isCreated()).isTrue();
        assertThat(mongo.isRunning()).isTrue();
    }

    @Test
    void givenUrlExists_whenFindByShortUrl_thenGetUrl() {
        var saved = repository.save(new UrlData("short-url", "long-url", "user-id"));

        var url = repository.findByShortUrl("short-url");
        assertThat(url).isNotNull();
        assertThat(url).isPresent();
        assertThat(url.get().getShortUrl()).isEqualTo("short-url");
        assertThat(url.get().getLongUrl()).isEqualTo("long-url");
        assertThat(url.get().getUserid()).isEqualTo("user-id");

        repository.delete(saved);
    }

    @Test
    void givenNonExistingUrl_whenFindByShortUrl_thenReturnNotPresent() {
        var url = repository.findByShortUrl("short-url-not-existing");
        assertThat(url).isNotNull();
        assertThat(url).isEmpty();
    }
}