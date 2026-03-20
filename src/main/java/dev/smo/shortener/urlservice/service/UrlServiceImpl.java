package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Optional<UrlData> getByShortUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl);
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
    public Optional<UrlData> saveUrl(String shortUrl, String longUrl, String userid) {
        var urlToSave = new UrlData(shortUrl, longUrl, userid);
        return Optional.of(urlRepository.save(urlToSave));
    }

    @Override
    public Optional<UrlData> updateUrl(String id, String shortUrl, String longUrl) {
        var existing = urlRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        // if url is updated create a new one `active` and keep the old with `inactive`
        var updated = new UrlData(id, shortUrl, longUrl, existing.get().getUser(), existing.get().getCreated(), LocalDateTime.now());
        var saved = urlRepository.save(updated);
        return Optional.of(saved);
    }

}
