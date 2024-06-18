package io.jumper.urlservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jumper.urlservice.model.UrlData;
import io.jumper.urlservice.service.UrlService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(UrlServiceController.class)
class UrlServiceControllerTest {

    @MockBean
    UrlService urlService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void get_ShouldReturnUrlData_WhenShortUrlExists() throws Exception {
        var now = LocalDateTime.of(2024,1,2,3,4,5);
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("0abcd")
                .longUrl("http://abc.io/")
                .userid("user-id")
                .created(now)
                .updated(now)
                .build();
        given(urlService.getLongUrl(any())).willReturn(Optional.of(urlData));

        mockMvc.perform(get(UrlServiceController.SERVICE_API_V1 + "/" + urlData.getShortUrl()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(urlData.getId())))
                .andExpect(jsonPath("$.shortUrl", is(urlData.getShortUrl())))
                .andExpect(jsonPath("$.longUrl", is(urlData.getLongUrl())))
                .andExpect(jsonPath("$.userid", is(urlData.getUserid())))
                .andExpect(jsonPath("$.created", is("2024-01-02T03:04:05")))
                .andExpect(jsonPath("$.updated", is("2024-01-02T03:04:05")));
        // Assure we call our service with the right parameter
        then(urlService).should().getLongUrl(urlData.getShortUrl());
    }

    @Test
    void get_ShouldReturnNotFound_WhenShortUrlDoesNotExist() throws Exception {
        var shortUrl = "0abc";
        given(urlService.getLongUrl(any())).willReturn(Optional.empty());
        mockMvc.perform(get(UrlServiceController.SERVICE_API_V1 + "/" + shortUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    void post_ShouldCreateNewUrl_WhenUrlDataIsValid() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .userid("user-id")
                .build();
        var jsonData = objectMapper.writeValueAsString(UrlData.builder()
                        .shortUrl(urlData.getShortUrl())
                        .longUrl(urlData.getLongUrl())
                        .userid(urlData.getUserid())
                        .build());

        given(urlService.saveUrl(any(), any(), any())).willReturn(Optional.of(urlData));

        mockMvc.perform(post(UrlServiceController.SERVICE_API_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(urlData.getId())));

        // Assure @RequestBody IS set...
        then(urlService).should().saveUrl(eq(urlData.getShortUrl()), eq(urlData.getLongUrl()), eq(urlData.getUserid()));
    }

    @Test
    void put_ShouldUpdateUrlData_WhenUrlDataIsValid() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .userid("user-id")
                .build();
        given(urlService.updateUrl(urlData.getId(), urlData.getShortUrl(), urlData.getLongUrl())).willReturn(Optional.of(urlData));
        mockMvc.perform(put(UrlServiceController.SERVICE_API_V1 + "/" + urlData.getId()).contentType(MediaType.APPLICATION_JSON)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(urlData)))
                .andExpect(status().isOk());
        verify(urlService, times(1)).updateUrl(urlData.getId(), urlData.getShortUrl(), urlData.getLongUrl());
        verifyNoMoreInteractions(urlService);
    }

    @Test
    void put_ShouldThrowNotFound_WhenUrlWithIdNotExists() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .userid("user-id")
                .build();
        given(urlService.updateUrl(urlData.getId(), urlData.getShortUrl(), urlData.getLongUrl())).willReturn(Optional.empty());
        mockMvc.perform(put(UrlServiceController.SERVICE_API_V1 + "/" + urlData.getId()).contentType(MediaType.APPLICATION_JSON)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(urlData)))
                .andExpect(status().isNotFound());
        verify(urlService, times(1)).updateUrl(urlData.getId(), urlData.getShortUrl(), urlData.getLongUrl());
        verifyNoMoreInteractions(urlService);
    }
}