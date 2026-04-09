package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.model.UrlStatus;
import dev.smo.shortener.urlservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public Optional<UrlData> getById(String id) {
        return urlRepository.findById(id);
    }

    @Override
    public Optional<UrlData> getByDomainAndShortUrl(String domain, String shortUrl) {
        return urlRepository.findByDomainAndShortUrl(normalizeDomain(domain), shortUrl);
    }

    @Override
    public List<UrlData> getAllUrls() {
        return urlRepository.findAll();
    }

    @Override
    public List<UrlData> getUrlsByUser(String user) {
        return urlRepository.findByUser(user);
    }

    @Override
    public List<UrlData> getUrlsByDomain(String domain) {
        return urlRepository.findByDomain(normalizeDomain(domain));
    }

    @Override
    public Optional<UrlData> saveUrl(String domain, String shortUrl, String longUrl, String userId) {
        var normalizedDomain = normalizeDomain(domain);

        var urlToSave = new UrlData(
                normalizedDomain,
                shortUrl,
                longUrl,
                userId
        );

        try {
            return Optional.of(urlRepository.save(urlToSave));
        } catch (DuplicateKeyException e) {
            // (domain, shortUrl) already exists
            return Optional.empty();
        }
    }

    @Override
    public Optional<UrlData> updateUrl(String id, String domain, String shortUrl, String longUrl, String status) {
        var existingOpt = urlRepository.findById(id);

        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        var existing = existingOpt.get();
        var normalizedDomain = normalizeDomain(domain);

        var updated = new UrlData(
                id,
                normalizedDomain,
                shortUrl,
                longUrl,
                existing.getUser(),
                UrlStatus.fromString(status).toString(),
                existing.getCreated(),
                LocalDateTime.now()
        );

        try {
            return Optional.of(urlRepository.save(updated));
        } catch (DuplicateKeyException e) {
            // conflict with existing (domain + shortUrl)
            return Optional.empty();
        }
    }

    // --- helper ---

    private String normalizeDomain(String domain) {
        if (domain == null) return null;

        return domain.toLowerCase()
                .replace("http://", "")
                .replace("https://", "")
                .replaceAll("/+$", "");
    }
}