package dev.smo.shortener.urlservice.repository;

import dev.smo.shortener.urlservice.model.UrlData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<UrlData, String> {

    Optional<UrlData> findByShortUrl(String shortUrl);

    Page<UrlData> findByUser(String user, Pageable pageable);
}

