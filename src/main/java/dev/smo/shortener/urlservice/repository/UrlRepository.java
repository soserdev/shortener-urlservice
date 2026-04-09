package dev.smo.shortener.urlservice.repository;

import dev.smo.shortener.urlservice.model.UrlData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<UrlData, String> {

    Optional<UrlData> findByDomainAndShortUrl(String domain, String shortUrl);

    List<UrlData> findByUser(String user);

    List<UrlData> findByDomain(String domain);
}