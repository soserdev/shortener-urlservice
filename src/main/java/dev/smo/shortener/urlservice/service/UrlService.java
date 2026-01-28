package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UrlService {

    Page<UrlData> getUrls(int pageNumber, int pageSize);

    Optional<UrlData> getLongUrl(final String shortUrl);

    Optional<UrlData> saveUrl(String shortUrl, String longUrl, String userid);

    Optional<UrlData> updateUrl(String id, String shortUrl, String longUrl);
}
