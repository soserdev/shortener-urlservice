package io.jumper.urlservice.service;

import io.jumper.urlservice.exception.UrlServiceException;
import io.jumper.urlservice.model.UrlData;
import io.jumper.urlservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public Optional<UrlData> getLongUrl(String shortUrl) {
        var url = urlRepository.findByShortUrl(shortUrl);
        return Optional.ofNullable(url);
    }

    @Override
    public Optional<UrlData> saveUrl(String shortUrl, String longUrl, String userid) {
        if (urlRepository.findByShortUrl(shortUrl) != null) {
            throw new UrlServiceException("Short url is not unique!");
        }
        // short url is unique... let's store it...
        var url = new UrlData(shortUrl, longUrl, userid);
        return Optional.ofNullable(urlRepository.save(url));
    }

}
