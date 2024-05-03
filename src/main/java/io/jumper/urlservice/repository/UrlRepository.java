package io.jumper.urlservice.repository;

import io.jumper.urlservice.model.UrlData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends MongoRepository<UrlData, String> {

    UrlData findByShortUrl(String shortUrlPath);

}
