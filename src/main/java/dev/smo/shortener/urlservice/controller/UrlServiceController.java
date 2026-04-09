package dev.smo.shortener.urlservice.controller;

import dev.smo.shortener.urlservice.exception.ResourceNotFoundException;
import dev.smo.shortener.urlservice.exception.UrlServiceException;
import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/urls")
public class UrlServiceController {

    static final String SERVICE_API_V1 = "/api/v1/urls";

    final private UrlService urlService;

    @GetMapping("/{id}")
    public ResponseEntity<UrlData> getById(@PathVariable String id) {
        var url = urlService.getById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource with id: '" + id + "' not found!"));

        return ResponseEntity.ok(url);
    }

    @GetMapping("/short/{domain}/{shortUrl}")
    public ResponseEntity<UrlData> getByShortUrl(
            @PathVariable String domain,
            @PathVariable String shortUrl) {

        var url = urlService.getByDomainAndShortUrl(domain, shortUrl)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource for domain: '" + domain + "' and shortUrl: '" + shortUrl + "' not found!"
                        ));

        return ResponseEntity.ok(url);
    }

    @GetMapping
    public ResponseEntity<List<UrlData>> getUrls(
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String domain) {

        if (user != null) {
            return ResponseEntity.ok(urlService.getUrlsByUser(user));
        }

        if (domain != null) {
            return ResponseEntity.ok(urlService.getUrlsByDomain(domain));
        }

        return ResponseEntity.ok(urlService.getAllUrls());
    }

    @PostMapping
    public ResponseEntity<UrlData> create(@RequestBody @Validated UrlData url) {

        var savedUrl = urlService
                .saveUrl(url.getDomain(), url.getShortUrl(), url.getLongUrl(), url.getUser())
                .orElseThrow(() -> new UrlServiceException("Url not created (possibly duplicate)!"));

        return new ResponseEntity<>(savedUrl, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UrlData> update(
            @PathVariable String id,
            @RequestBody @Valid UrlData url) {

        var updatedUrl = urlService
                .updateUrl(
                        id,
                        url.getDomain(),
                        url.getShortUrl(),
                        url.getLongUrl(),
                        url.getStatus()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource with id: '" + id + "' not found or conflict occurred!"
                        ));

        return ResponseEntity.ok(updatedUrl);
    }
}