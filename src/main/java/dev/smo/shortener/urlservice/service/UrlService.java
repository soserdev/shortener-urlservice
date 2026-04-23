package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UrlService {

    Optional<UrlData> getById(String id);

    Optional<UrlData> getByShortUrl(final String shortUrl);

    Page<UrlData> getAllUrls(Pageable pageable);

    Page<UrlData> getUrlsByUser(String user, Pageable pageable);

    Optional<UrlData> saveUrl(String shortUrl, String longUrl, String userid);

    Optional<UrlData> updateUrl(String id, String shortUrl, String longUrl, String status);
}