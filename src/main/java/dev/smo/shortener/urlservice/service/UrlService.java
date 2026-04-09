package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;

import java.util.List;
import java.util.Optional;

public interface UrlService {

    Optional<UrlData> getById(String id);

    Optional<UrlData> getByDomainAndShortUrl(String domain, String shortUrl);

    List<UrlData> getAllUrls();

    List<UrlData> getUrlsByUser(String user);

    List<UrlData> getUrlsByDomain(String domain);

    Optional<UrlData> saveUrl(String domain, String shortUrl, String longUrl, String userId);

    Optional<UrlData> updateUrl(String id, String domain, String shortUrl, String longUrl, String status);
}