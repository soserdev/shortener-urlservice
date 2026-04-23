package dev.smo.shortener.urlservice.controller;

import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.model.UrlStatus;
import dev.smo.shortener.urlservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
        var now = LocalDateTime.of(2024, 1, 2, 3, 4, 5);

        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("0abcd")
                .longUrl("http://abc.io/")
                .user("user-id")
                .created(now)
                .updated(now)
                .build();

        given(urlService.getByShortUrl(any()))
                .willReturn(Optional.of(urlData));

        mockMvc.perform(get(UrlServiceController.SERVICE_API_V1 + "/short/" + urlData.getShortUrl()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl", is(urlData.getShortUrl())))
                .andExpect(jsonPath("$.longUrl", is(urlData.getLongUrl())));

        then(urlService).should().getByShortUrl(urlData.getShortUrl());
    }

    @Test
    void getUrlNotExisting() throws Exception {
        given(urlService.getByShortUrl(any())).willReturn(Optional.empty());

        mockMvc.perform(get(UrlServiceController.SERVICE_API_V1 + "/short/0abc"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUrls_withPagination() throws Exception {
        var url1 = UrlData.builder()
                .id("1")
                .shortUrl("s1")
                .longUrl("l1")
                .user("user1")
                .build();

        var url2 = UrlData.builder()
                .id("2")
                .shortUrl("s2")
                .longUrl("l2")
                .user("user1")
                .build();

        var page = new PageImpl<>(
                List.of(url1, url2),
                PageRequest.of(0, 2),
                2
        );

        given(urlService.getAllUrls(any()))
                .willReturn(page);

        mockMvc.perform(get(UrlServiceController.SERVICE_API_V1)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content[0].shortUrl", is("s1")));

        then(urlService).should().getAllUrls(any());
    }

    @Test
    void getUrlsByUser_withPagination() throws Exception {
        var url = UrlData.builder()
                .id("1")
                .shortUrl("s1")
                .longUrl("l1")
                .user("user1")
                .build();

        var page = new PageImpl<>(
                List.of(url),
                PageRequest.of(0, 10),
                1
        );

        given(urlService.getUrlsByUser(eq("user1"), any()))
                .willReturn(page);

        mockMvc.perform(get(UrlServiceController.SERVICE_API_V1)
                        .param("user", "user1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].user", is("user1")));

        then(urlService).should().getUrlsByUser(eq("user1"), any());
    }

    @Test
    void createNewUrl() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .user("user-id")
                .build();

        var jsonData = objectMapper.writeValueAsString(
                UrlData.builder()
                        .shortUrl(urlData.getShortUrl())
                        .longUrl(urlData.getLongUrl())
                        .user(urlData.getUser())
                        .build()
        );

        given(urlService.saveUrl(any(), any(), any()))
                .willReturn(Optional.of(urlData));

        mockMvc.perform(post(UrlServiceController.SERVICE_API_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(urlData.getId())));

        then(urlService).should()
                .saveUrl(eq(urlData.getShortUrl()), eq(urlData.getLongUrl()), eq(urlData.getUser()));
    }

    @Test
    void updateExistingUrl() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .user("user-id")
                .status(UrlStatus.ACTIVE.toString())
                .build();

        given(urlService.updateUrl(
                urlData.getId(),
                urlData.getShortUrl(),
                urlData.getLongUrl(),
                urlData.getStatus()
        )).willReturn(Optional.of(urlData));

        mockMvc.perform(put(UrlServiceController.SERVICE_API_V1 + "/" + urlData.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlData)))
                .andExpect(status().isOk());

        then(urlService).should()
                .updateUrl(urlData.getId(), urlData.getShortUrl(), urlData.getLongUrl(), urlData.getStatus());

        verifyNoMoreInteractions(urlService);
    }

    @Test
    void updateNotExistingUrlReturnsNotFound() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .user("user-id")
                .status(UrlStatus.ACTIVE.toString())
                .build();

        given(urlService.updateUrl(
                urlData.getId(),
                urlData.getShortUrl(),
                urlData.getLongUrl(),
                urlData.getStatus()
        )).willReturn(Optional.empty());

        mockMvc.perform(put(UrlServiceController.SERVICE_API_V1 + "/" + urlData.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlData)))
                .andExpect(status().isNotFound());

        then(urlService).should()
                .updateUrl(urlData.getId(), urlData.getShortUrl(), urlData.getLongUrl(), urlData.getStatus());

        verifyNoMoreInteractions(urlService);
    }
}