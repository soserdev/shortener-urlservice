package dev.smo.shortener.urlservice.service;

import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.print.Book;
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
    public Page<UrlData> getUrls(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.DESC, "created")
        );
        return urlRepository.findAll(pageable);
    }

    @Override
    public Optional<UrlData> getLongUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl);
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
        var updated = new UrlData(id, shortUrl, longUrl, existing.get().getUserid(), existing.get().getCreated(), LocalDateTime.now());
        var saved = urlRepository.save(updated);
        return Optional.ofNullable(saved);
    }
}
