package dev.smo.shortener.urlservice.repository;

import dev.smo.shortener.urlservice.TestcontainersConfiguration;
import dev.smo.shortener.urlservice.model.UrlData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.mongodb.MongoDBContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@DataMongoTest
class UrlRepositoryTest {

    @Autowired
    MongoDBContainer mongo;

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