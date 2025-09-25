package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
    void saveUrl_ShouldSaveUrlData_WhenShortUrlIsUnique() {
        when(urlRepository.save(any(UrlData.class))).thenReturn(urlData);

        Optional<UrlData> result = urlService.saveUrl("shortUrl", "http://longurl.com", "user123");

        assertTrue(result.isPresent());
        assertEquals("shortUrl", result.get().getShortUrl());
        assertEquals("http://longurl.com", result.get().getLongUrl());
        verify(urlRepository, times(1)).save(any(UrlData.class));
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void updateUrl_ShouldSaveUpdatedUrlData_WhenUrlDataExists() {
        var id = UUID.randomUUID().toString();
        var now = LocalDateTime.now();
        var yesterday = now.minusDays(1);
        var existing = new UrlData(id, "shortUrl-old", "http://longurl-old.com", "user123", yesterday, yesterday);
        var updated = new UrlData(id, "shortUrl", "http://longurl.com", "user123", yesterday, now);

        when(urlRepository.findById(id)).thenReturn(Optional.of(existing));
        when(urlRepository.save(any(UrlData.class))).thenReturn(updated);

        Optional<UrlData> result = urlService.updateUrl(id, "shortUrl", "http://longurl.com");

        assertTrue(result.isPresent());
        assertEquals("shortUrl", result.get().getShortUrl());
        assertEquals("http://longurl.com", result.get().getLongUrl());
        verify(urlRepository, times(1)).findById(any(String.class));
        verify(urlRepository, times(1)).save(any(UrlData.class));
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void updateUrl_ShouldReturnOptionalEmpty_WhenUrlDataNotExists() {
        var id = UUID.randomUUID().toString();

        when(urlRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UrlData> result = urlService.updateUrl(id, "shortUrl", "http://longurl.com");

        assertTrue(result.isEmpty());
    }
}