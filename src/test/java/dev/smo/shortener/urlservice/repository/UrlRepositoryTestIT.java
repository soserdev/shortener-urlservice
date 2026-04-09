package dev.smo.shortener.urlservice.repository;

import dev.smo.shortener.urlservice.TestcontainersConfiguration;
import dev.smo.shortener.urlservice.model.UrlData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.mongodb.MongoDBContainer;

import java.util.List;
import java.util.Optional;

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

    @Test
    void connectionIsEstablished() {
        assertThat(mongo.isCreated()).isTrue();
        assertThat(mongo.isRunning()).isTrue();
    }

    @Test
    void findByDomainAndShortUrl() {
        var saved = repository.save(new UrlData(
                "mydomain.com",
                "short-url",
                "http://long-url.com",
                "user-id"
        ));

        Optional<UrlData> url = repository.findByDomainAndShortUrl("mydomain.com", "short-url");
        assertThat(url).isPresent();
        assertThat(url.get().getDomain()).isEqualTo("mydomain.com");
        assertThat(url.get().getShortUrl()).isEqualTo("short-url");
        assertThat(url.get().getLongUrl()).isEqualTo("http://long-url.com");
        assertThat(url.get().getUser()).isEqualTo("user-id");

        repository.delete(saved);
    }

    @Test
    void findByDomainAndShortUrlNotExisting() {
        Optional<UrlData> url = repository.findByDomainAndShortUrl("mydomain.com", "nonexistent");
        assertThat(url).isEmpty();
    }

    @Test
    void findByUser() {
        repository.save(new UrlData("mydomain.com", "short1", "http://long1.com", "user-id"));
        repository.save(new UrlData("example.com", "short2", "http://long2.com", "user-id"));

        List<UrlData> urls = repository.findByUser("user-id");
        assertThat(urls).hasSize(2);
        assertThat(urls).extracting(UrlData::getUser).containsOnly("user-id");
    }

    @Test
    void findByDomain() {
        repository.save(new UrlData("mydomain.com", "short1", "http://long1.com", "user1"));
        repository.save(new UrlData("mydomain.com", "short2", "http://long2.com", "user2"));
        repository.save(new UrlData("example.com", "short3", "http://long3.com", "user1"));

        List<UrlData> urls = repository.findByDomain("mydomain.com");
        assertThat(urls).hasSize(2);
        assertThat(urls).extracting(UrlData::getDomain).containsOnly("mydomain.com");
    }
}