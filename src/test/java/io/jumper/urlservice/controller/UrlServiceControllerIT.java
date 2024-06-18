package io.jumper.urlservice.controller;

import io.jumper.urlservice.TestcontainersConfiguration;
import io.jumper.urlservice.model.UrlData;
import io.jumper.urlservice.repository.UrlRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class UrlServiceControllerIT {

    @Autowired
    MongoDBContainer mongo;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UrlRepository urlRepository;

    @Test
    void contextLoads() {
        assertThat(mongo.isCreated()).isTrue();
        assertThat(mongo.isRunning()).isTrue();
    }

    @Test
    void shouldCreateNewUrl_WhenUrlIsValid() throws JSONException {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var url = new JSONObject();
        url.put("shortUrl", "short-url");
        url.put("longUrl", "http://long-url/");
        url.put("userid", "user-id");

        ResponseEntity<UrlData> response = restTemplate.exchange(UrlServiceController.SERVICE_API_V1, HttpMethod.POST, new HttpEntity<String>(url.toString(), headers), UrlData.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getShortUrl()).isEqualTo("short-url");
        assertThat(response.getBody().getLongUrl()).isEqualTo("http://long-url/");
        assertThat(response.getBody().getUserid()).isEqualTo("user-id");
        assertThat(response.getBody().getId()).isNotBlank();
        assertThat(response.getBody().getCreated()).isNotNull();
        assertThat(response.getBody().getUpdated()).isNotNull();

        // check if url is stored in repository
        Optional<UrlData> found = urlRepository.findById(response.getBody().getId());
        assertThat(found).isNotNull();
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getShortUrl()).isEqualTo("short-url");
        assertThat(found.get().getLongUrl()).isEqualTo("http://long-url/");
        assertThat(found.get().getUserid()).isEqualTo("user-id");
        assertThat(found.get().getCreated()).isNotNull();
        assertThat(found.get().getUpdated()).isNotNull();
    }

    @Test
    void shouldNotCreateNewUrl_WhenUrlIsInvalid() throws JSONException {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var url = new JSONObject();
        url.put("shortUrl", "");

        ResponseEntity<UrlData> response = restTemplate.exchange(UrlServiceController.SERVICE_API_V1, HttpMethod.POST, new HttpEntity<String>(url.toString(), headers), UrlData.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldUpdateUrl_WhenUrlIsValid() throws JSONException {
        var existing = urlRepository.save(new UrlData("short-url-old", "long-url-old", "user-id"));

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var url = new JSONObject();
        url.put("id", existing.getId());
        url.put("shortUrl", "short-url");
        url.put("longUrl", "http://long-url/");
        url.put("userid", "user-id");

        var response = restTemplate.exchange(UrlServiceController.SERVICE_API_V1 + "/" + existing.getId(), HttpMethod.PUT,
                new HttpEntity<String>(url.toString(), headers), UrlData.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getShortUrl()).isEqualTo("short-url");
        assertThat(response.getBody().getLongUrl()).isEqualTo("http://long-url/");
        assertThat(response.getBody().getUserid()).isEqualTo("user-id");

        // check if url is stored in repository
        Optional<UrlData> found = urlRepository.findById(existing.getId());
        assertThat(found).isNotNull();
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getShortUrl()).isEqualTo("short-url");
        assertThat(found.get().getLongUrl()).isEqualTo("http://long-url/");
        assertThat(found.get().getUserid()).isEqualTo("user-id");
    }
}