package io.jumper.urlservice.service;

import io.jumper.urlservice.model.UrlData;

import java.util.Optional;

public interface UrlService {

    Optional<UrlData> getLongUrl(final String shortUrl);

    Optional<UrlData> saveUrl(String shortUrl, String longUrl, String userid);
}
