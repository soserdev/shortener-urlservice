package io.jumper.urlservice.controller;

import io.jumper.urlservice.exception.ResourceNotFoundException;
import io.jumper.urlservice.exception.UrlServiceException;
import io.jumper.urlservice.model.UrlData;
import io.jumper.urlservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController()
@RequiredArgsConstructor
public class UrlServiceController {

    static final String SERVICE_API_V1 = "/api/v1/urlservice";

    final private UrlService urlService;

    @GetMapping(SERVICE_API_V1 + "/{shortUrl}")
    public ResponseEntity<UrlData> getLongUrl(@PathVariable String shortUrl) {
        var url = urlService.getLongUrl(shortUrl)
                .orElseThrow(() -> new ResourceNotFoundException("Resource for shortUrl: '" + shortUrl + "' not found!"));
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @PostMapping(SERVICE_API_V1)
    public ResponseEntity<UrlData> create(@RequestBody @Validated UrlData url) {
        var savedUrl = urlService.saveUrl(url.getShortUrl(), url.getLongUrl(),url.getUserid())
                .orElseThrow(() -> new UrlServiceException("Url not created!"));

        return new ResponseEntity<>(savedUrl, HttpStatus.CREATED);
    }

    @PutMapping(SERVICE_API_V1 + "/{id}")
    public ResponseEntity<UrlData> update(@PathVariable String id, @RequestBody @Valid UrlData url) {
        var updatedUrl = urlService.updateUrl(id, url.getShortUrl(), url.getLongUrl())
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id: '" + id + "' not found!"));
        return new ResponseEntity<>(updatedUrl, HttpStatus.OK);
    }
}
