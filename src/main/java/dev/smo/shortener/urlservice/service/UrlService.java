package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;

import java.util.Optional;

public interface UrlService {

    Optional<UrlData> getLongUrl(final String shortUrl);

    Optional<UrlData> saveUrl(String shortUrl, String longUrl, String userid);

    Optional<UrlData> updateUrl(String id, String shortUrl, String longUrl);
}
