package io.jumper.urlservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jumper.urlservice.model.UrlData;
import io.jumper.urlservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void getLongUrlFound() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("0abcd")
                .longUrl("http://abc.io/")
                .build();
        given(urlService.getLongUrl(any())).willReturn(Optional.of(urlData));
        var actions = mockMvc.perform(get(UrlServiceController.SERVICE_API_V1 + "/" + urlData.getShortUrl()));
        actions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(urlData.getId())))
                .andExpect(jsonPath("$.shortUrl", is(urlData.getShortUrl())))
                .andExpect(jsonPath("$.longUrl", is(urlData.getLongUrl())));
        // Assure we call our service with the right parameter
        then(urlService).should().getLongUrl(urlData.getShortUrl());
    }

    @Test
    void getLongUrlNotFound() throws Exception {
        var shortUrl = "0abc";
        given(urlService.getLongUrl(any())).willReturn(Optional.empty());
        var actions = mockMvc.perform(get(UrlServiceController.SERVICE_API_V1 + "/" + shortUrl));
        actions.andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        var urlData = UrlData.builder()
                .id(UUID.randomUUID().toString())
                .shortUrl("short-url")
                .longUrl("http://longurl.com/")
                .build();
        var jsonData = objectMapper.writeValueAsString(UrlData.builder().shortUrl(urlData.getShortUrl()).longUrl(urlData.getLongUrl()).build());

        given(urlService.saveUrl(any(), any(), any())).willReturn(Optional.of(urlData));
        var resultActions = mockMvc.perform(post(UrlServiceController.SERVICE_API_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData));

        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(urlData.getId())));
        // Assure @RequestBody IS set...
        then(urlService).should().saveUrl(eq(urlData.getShortUrl()), eq(urlData.getLongUrl()), eq(urlData.getUserid()));
    }
}