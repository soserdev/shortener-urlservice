package io.jumper.urlservice.controller;

import io.jumper.urlservice.model.UrlData;
import io.jumper.urlservice.repository.UrlRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class UrlServiceControllerIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.11");

    @Autowired
    UrlServiceController urlServiceController;

    @Autowired
    UrlRepository urlRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldCreateUrl() throws JSONException {
        var urlData = UrlData.builder()
                .longUrl("http://long-url/")
                .shortUrl("short-url")
                .userid("userid")
                .build();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var url = new JSONObject();
        url.put("shortUrl", "short-url");
        url.put("longUrl", "https://long-url/");
        url.put("userid", "user-id");

    }

    /*
    @Test
    void createFindAndDelete() {
        var urlData = UrlData.builder()
                .longUrl("http://long-url/")
                .shortUrl("short-url")
                .userid("userid")
                .build();
        var responseEntity = urlServiceController.create(urlData);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getId()).isNotNull();
        assertThat(responseEntity.getBody().getShortUrl()).isEqualTo(urlData.getShortUrl());
        assertThat(responseEntity.getBody().getLongUrl()).isEqualTo(urlData.getLongUrl());
        assertThat(responseEntity.getBody().getUserid()).isEqualTo(urlData.getUserid());
        assertThat(responseEntity.getBody().getCreated()).isNotNull();
        assertThat(responseEntity.getBody().getUpdated()).isNotNull();

        Optional<UrlData> found = urlRepository.findById(responseEntity.getBody().getId());
        assertThat(found).isNotNull();
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getShortUrl()).isEqualTo((urlData.getShortUrl()));
        assertThat(found.get().getLongUrl()).isEqualTo(urlData.getLongUrl());
        assertThat(found.get().getUserid()).isEqualTo(urlData.getUserid());
        assertThat(found.get().getCreated()).isNotNull();

        urlRepository.delete(found.get());
    }
  */

}
