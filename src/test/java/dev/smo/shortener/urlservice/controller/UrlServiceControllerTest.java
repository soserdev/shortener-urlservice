package dev.smo.shortener.urlservice.controller;

import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.model.UrlStatus;
import dev.smo.shortener.urlservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UrlServiceController.class, properties = {
        "mongock.enabled=false"
})
class UrlServiceControllerTest {

    @MockitoBean
    UrlService urlService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getUrlByShort() throws Exception {
        var now = LocalDateTime.of(2024,1,2,3,4,5);

        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .domain("mydomain.com")
                .shortUrl("0abcd")
                .longUrl("http://abc.io/")
                .user("user-id")
                .created(now)
                .updated(now)
                .build();

        given(urlService.getByDomainAndShortUrl(any(), any()))
                .willReturn(Optional.of(urlData));

        mockMvc.perform(get(UrlServiceController.SERVICE_API_V1
                        + "/short/" + urlData.getDomain()
                        + "/" + urlData.getShortUrl()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(urlData.getId())))
                .andExpect(jsonPath("$.domain", is(urlData.getDomain())))
                .andExpect(jsonPath("$.shortUrl", is(urlData.getShortUrl())))
                .andExpect(jsonPath("$.longUrl", is(urlData.getLongUrl())))
                .andExpect(jsonPath("$.user", is(urlData.getUser())))
                .andExpect(jsonPath("$.created", is("2024-01-02T03:04:05")))
                .andExpect(jsonPath("$.updated", is("2024-01-02T03:04:05")));

        then(urlService).should()
                .getByDomainAndShortUrl(urlData.getDomain(), urlData.getShortUrl());
    }

    @Test
    void getUrlNotExisting() throws Exception {
        given(urlService.getByDomainAndShortUrl(any(), any()))
                .willReturn(Optional.empty());

        mockMvc.perform(get(UrlServiceController.SERVICE_API_V1 + "/short/mydomain.com/0abc"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNewUrl() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .domain("mydomain.com")
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .user("user-id")
                .build();

        var jsonData = objectMapper.writeValueAsString(
                UrlData.builder()
                        .domain(urlData.getDomain())
                        .shortUrl(urlData.getShortUrl())
                        .longUrl(urlData.getLongUrl())
                        .user(urlData.getUser())
                        .build()
        );

        given(urlService.saveUrl(any(), any(), any(), any()))
                .willReturn(Optional.of(urlData));

        mockMvc.perform(post(UrlServiceController.SERVICE_API_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(urlData.getId())));

        then(urlService).should()
                .saveUrl(
                        eq(urlData.getDomain()),
                        eq(urlData.getShortUrl()),
                        eq(urlData.getLongUrl()),
                        eq(urlData.getUser())
                );
    }

    @Test
    void updateExistingUrl() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .domain("mydomain.com")
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .user("user-id")
                .status(UrlStatus.ACTIVE.toString())
                .build();

        given(urlService.updateUrl(
                urlData.getId(),
                urlData.getDomain(),
                urlData.getShortUrl(),
                urlData.getLongUrl(),
                urlData.getStatus()
        )).willReturn(Optional.of(urlData));

        mockMvc.perform(put(UrlServiceController.SERVICE_API_V1 + "/" + urlData.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlData)))
                .andExpect(status().isOk());

        verify(urlService, times(1)).updateUrl(
                urlData.getId(),
                urlData.getDomain(),
                urlData.getShortUrl(),
                urlData.getLongUrl(),
                urlData.getStatus()
        );

        verifyNoMoreInteractions(urlService);
    }

    @Test
    void updateNotExistingUrlReturnsNotFound() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .domain("mydomain.com")
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .user("user-id")
                .status(UrlStatus.ACTIVE.toString())
                .build();

        given(urlService.updateUrl(
                urlData.getId(),
                urlData.getDomain(),
                urlData.getShortUrl(),
                urlData.getLongUrl(),
                urlData.getStatus()
        )).willReturn(Optional.empty());

        mockMvc.perform(put(UrlServiceController.SERVICE_API_V1 + "/" + urlData.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlData)))
                .andExpect(status().isNotFound());

        verify(urlService, times(1)).updateUrl(
                urlData.getId(),
                urlData.getDomain(),
                urlData.getShortUrl(),
                urlData.getLongUrl(),
                urlData.getStatus()
        );

        verifyNoMoreInteractions(urlService);
    }
}