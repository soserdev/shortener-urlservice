package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.model.UrlStatus;
import dev.smo.shortener.urlservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Optional<UrlData> getByShortUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl);
    }

    @Override
    public Page<UrlData> getAllUrls(Pageable pageable) {
        return urlRepository.findAll(pageable);
    }

    @Override
    public Page<UrlData> getUrlsByUser(String user, Pageable pageable) {
        return urlRepository.findByUser(user, pageable);
    }

    @Override
    public Optional<UrlData> saveUrl(String shortUrl, String longUrl, String userid) {
        var urlToSave = new UrlData(shortUrl, longUrl, userid);
        return Optional.of(urlRepository.save(urlToSave));
    }

    @Override
    public Optional<UrlData> updateUrl(String id, String shortUrl, String longUrl, String status) {
        var existingOpt = urlRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        var existing = existingOpt.get();

        if (shortUrl != null && !shortUrl.isEmpty()) {
            existing.setShortUrl(shortUrl);
        }

        if (longUrl != null && !longUrl.isEmpty()) {
            existing.setLongUrl(longUrl);
        }

        if (status != null && !status.isEmpty()) {
            existing.setStatus(UrlStatus.fromString(status).toString());
        }

        existing.setUpdated(LocalDateTime.now());

        var saved = urlRepository.save(existing);
        return Optional.of(saved);
    }
}
