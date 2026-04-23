package dev.smo.shortener.urlservice.repository;

import dev.smo.shortener.urlservice.TestcontainersConfiguration;
import dev.smo.shortener.urlservice.model.UrlData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.mongodb.MongoDBContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@DataMongoTest(properties = {
        "mongock.enabled=false"
})
class UrlRepositoryTestIT {

    @Autowired
    MongoDBContainer mongo;

    @Autowired
    UrlRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void connectionIsEstablished() {
        assertThat(mongo.isCreated()).isTrue();
        assertThat(mongo.isRunning()).isTrue();
    }

    @Test
    void findByShortUrl() {
        var saved = repository.save(new UrlData("short-url", "long-url", "user-id"));

        var url = repository.findByShortUrl("short-url");
        assertThat(url).isNotNull();
        assertThat(url).isPresent();
        assertThat(url.get().getShortUrl()).isEqualTo("short-url");
        assertThat(url.get().getLongUrl()).isEqualTo("long-url");
        assertThat(url.get().getUser()).isEqualTo("user-id");

        repository.delete(saved);
    }

    @Test
    void findByShortUrlNotExisting() {
        var url = repository.findByShortUrl("short-url-not-existing");
        assertThat(url).isNotNull();
        assertThat(url).isEmpty();
    }

    @Test
    void findByUserWithPagination() {
        // given
        repository.save(new UrlData("s1", "l1", "user1"));
        repository.save(new UrlData("s2", "l2", "user1"));
        repository.save(new UrlData("s3", "l3", "user1"));
        repository.save(new UrlData("s4", "l4", "user2"));

        PageRequest pageable = PageRequest.of(0, 2);

        // when
        Page<UrlData> page = repository.findByUser("user1", pageable);

        // then
        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);

        assertThat(page.getContent())
                .extracting(UrlData::getUser)
                .containsOnly("user1");
    }
}