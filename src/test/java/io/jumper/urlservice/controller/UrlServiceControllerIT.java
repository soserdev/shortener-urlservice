package io.jumper.urlservice.controller;

import io.jumper.urlservice.model.UrlData;
import io.jumper.urlservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UrlServiceControllerIT {

    @Autowired
    UrlServiceController urlServiceController;

    @Autowired
    UrlRepository urlRepository;

    @Test
    @Transactional
    void create() {
        var longUrl = "http://abc.io/";
        var shortUrl = "=abc";
        var userid = "uid";
        var urldata = UrlData.builder()
                .longUrl(longUrl)
                .shortUrl(shortUrl)
                .userid(userid)
                .build();
        var responseEntity = urlServiceController.create(urldata);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getId()).isNotNull();
        assertThat(responseEntity.getBody().getLongUrl()).isEqualTo(longUrl);
        assertThat(responseEntity.getBody().getUserid()).isEqualTo(userid);
        assertThat(responseEntity.getBody().getCreated()).isNotNull();
        assertThat(responseEntity.getBody().getUpdated()).isNotNull();

        Optional<UrlData> urlData = urlRepository.findById(responseEntity.getBody().getId());
        assertThat(urlData).isNotNull();
        assertThat(urlData.isPresent()).isTrue();
        assertThat(urlData.get().getLongUrl()).isEqualTo(longUrl);
        assertThat(urlData.get().getShortUrl()).isEqualTo(shortUrl);
        assertThat(urlData.get().getUserid()).isEqualTo(userid);
        assertThat(urlData.get().getCreated()).isNotNull();
        assertThat(urlData.get().getUpdated()).isNotNull();
    }
}