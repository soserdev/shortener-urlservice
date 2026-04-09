package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.model.UrlStatus;
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

    private final String DOMAIN = "mydomain.com";

    @BeforeEach
    void setUp() {
        urlData = new UrlData(DOMAIN, "1fa", "http://example.com", "user123");
    }

    @Test
    void getByDomainAndShortUrlExists() {
        when(urlRepository.findByDomainAndShortUrl(DOMAIN, "1fa")).thenReturn(Optional.of(urlData));

        Optional<UrlData> result = urlService.getByDomainAndShortUrl(DOMAIN, "1fa");

        assertTrue(result.isPresent());
        assertEquals("http://example.com", result.get().getLongUrl());
        assertEquals(DOMAIN, result.get().getDomain());
        verify(urlRepository, times(1)).findByDomainAndShortUrl(eq(DOMAIN), eq("1fa"));
        Mockito.verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void getByDomainAndShortUrlNotExist() {
        when(urlRepository.findByDomainAndShortUrl(any(), any())).thenReturn(Optional.empty());

        Optional<UrlData> result = urlService.getByDomainAndShortUrl(DOMAIN, "1fa");

        assertEquals(Optional.empty(), result);
        verify(urlRepository, times(1)).findByDomainAndShortUrl(eq(DOMAIN), eq("1fa"));
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void saveUrl() {
        when(urlRepository.save(any(UrlData.class))).thenReturn(urlData);

        Optional<UrlData> result = urlService.saveUrl(DOMAIN, "1fa", "http://example.com", "user123");

        assertTrue(result.isPresent());
        assertEquals("1fa", result.get().getShortUrl());
        assertEquals("http://example.com", result.get().getLongUrl());
        assertEquals(DOMAIN, result.get().getDomain());
        verify(urlRepository, times(1)).save(any(UrlData.class));
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void updateUrlExists() {
        var id = UUID.randomUUID().toString();
        var now = LocalDateTime.now();
        var yesterday = now.minusDays(1);
        var existing = new UrlData(id, DOMAIN, "shortUrl-old", "http://longurl-old.com", "user123", UrlStatus.ACTIVE.toString(), yesterday, yesterday);
        var updated = new UrlData(id, DOMAIN, "shortUrl", "http://longurl.com", "user123", UrlStatus.INACTIVE.toString(), yesterday, now);

        when(urlRepository.findById(id)).thenReturn(Optional.of(existing));
        when(urlRepository.save(any(UrlData.class))).thenReturn(updated);

        Optional<UrlData> result = urlService.updateUrl(id, DOMAIN, "shortUrl", "http://longurl.com", UrlStatus.INACTIVE.toString());

        assertTrue(result.isPresent());
        assertEquals("shortUrl", result.get().getShortUrl());
        assertEquals("http://longurl.com", result.get().getLongUrl());
        assertEquals(UrlStatus.INACTIVE.toString(), result.get().getStatus());
        assertEquals(DOMAIN, result.get().getDomain());
        verify(urlRepository, times(1)).findById(any(String.class));
        verify(urlRepository, times(1)).save(any(UrlData.class));
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void updateUrlWhenUrlDataNotExists() {
        var id = UUID.randomUUID().toString();

        when(urlRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UrlData> result = urlService.updateUrl(id, DOMAIN, "shortUrl", "http://longurl.com", UrlStatus.INACTIVE.toString());

        assertTrue(result.isEmpty());
    }
}