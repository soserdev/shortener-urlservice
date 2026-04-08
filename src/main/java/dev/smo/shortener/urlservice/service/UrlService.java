package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;

import java.util.List;
import java.util.Optional;

public interface UrlService {

    Optional<UrlData> getById(String id);

    Optional<UrlData> getByShortUrl(final String shortUrl);

    List<UrlData> getAllUrls();

    List<UrlData> getUrlsByUser(String user);

    Optional<UrlData> saveUrl(String shortUrl, String longUrl, String userid);

    Optional<UrlData> updateUrl(String id, String shortUrl, String longUrl, String status);


}
