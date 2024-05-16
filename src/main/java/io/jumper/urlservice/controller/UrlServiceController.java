package io.jumper.urlservice.controller;

import io.jumper.urlservice.exception.UrlServiceException;
import io.jumper.urlservice.model.UrlData;
import io.jumper.urlservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController()
@RequiredArgsConstructor
public class UrlServiceController {

    static final String SERVICE_API_V1 = "/api/v1/urlservice";

    final private UrlService urlService;

    @GetMapping(SERVICE_API_V1 + "/{shortUrl}")
    public ResponseEntity<UrlData> getLongUrl(@PathVariable String shortUrl) {
        log.debug("'GET " + SERVICE_API_V1 + "/" + shortUrl);
        var url = urlService.getLongUrl(shortUrl);
        if (url.isEmpty()) {
            log.info("-> NOT Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.debug("-> originalPath: " + url.get().getLongUrl());
        return new ResponseEntity<>(url.get(), HttpStatus.OK);
    }

    @PostMapping(SERVICE_API_V1)
    public ResponseEntity<UrlData> create(@RequestBody UrlData url) {
        var savedUrl = urlService.saveUrl(url.getShortUrl(), url.getLongUrl(),url.getUserid());
        if (savedUrl.isEmpty()) {
            log.info("Url not created:" + url.getShortUrl());
            throw new UrlServiceException("Url not created");
        }
        return new ResponseEntity<>(savedUrl.get(), HttpStatus.CREATED);
    }
}
