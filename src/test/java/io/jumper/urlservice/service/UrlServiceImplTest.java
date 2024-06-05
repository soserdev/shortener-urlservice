package io.jumper.urlservice.service;

import io.jumper.urlservice.model.UrlData;
import io.jumper.urlservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    UrlRepository urlRepository;

    @InjectMocks
    UrlServiceImpl urlService;

    private UrlData urlData;

    @BeforeEach
    void setUp() {
        urlData = new UrlData("shortUrl", "http://longurl.com", "user123");
    }

    @Test
    void getLongUrl_ShouldReturnUrlData_WhenShortUrlExists() {
        when(urlRepository.findByShortUrl("shortUrl")).thenReturn(Optional.of(urlData));

        Optional<UrlData> result = urlService.getLongUrl("shortUrl");

        assertTrue(result.isPresent());
        assertEquals("http://longurl.com", result.get().getLongUrl());
        verify(urlRepository, times(1)).findByShortUrl(eq("shortUrl"));
        Mockito.verifyNoMoreInteractions(urlRepository);

    }
    @Test
    void getLongUrl_ShouldReturnEmptyOptional_WhenShortUrlDoesNotExist() {
        when(urlRepository.findByShortUrl(any())).thenReturn(Optional.empty());

        Optional<UrlData> result = urlService.getLongUrl("shortUrl");

        assertEquals(Optional.empty(), result);
        verify(urlRepository, times(1)).findByShortUrl(eq("shortUrl"));
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    @Transactional
    void saveUrl_ShouldSaveUrlData_WhenShortUrlIsUnique() {
        when(urlRepository.save(any(UrlData.class))).thenReturn(urlData);

        Optional<UrlData> result = urlService.saveUrl("shortUrl", "http://longurl.com", "user123");

        assertTrue(result.isPresent());
        assertEquals("shortUrl", result.get().getShortUrl());
        assertEquals("http://longurl.com", result.get().getLongUrl());
        verify(urlRepository, times(1)).save(any(UrlData.class));
        verifyNoMoreInteractions(urlRepository);
    }

}