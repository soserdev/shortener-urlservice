package io.jumper.urlservice.repository;

import io.jumper.urlservice.model.UrlData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<UrlData, String> {

    Optional<UrlData> findByShortUrl(String shortUrlPath);

}
