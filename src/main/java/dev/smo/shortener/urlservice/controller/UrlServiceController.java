package dev.smo.shortener.urlservice.controller;

import dev.smo.shortener.urlservice.exception.ResourceNotFoundException;
import dev.smo.shortener.urlservice.exception.UrlServiceException;
import dev.smo.shortener.urlservice.model.UrlData;
import dev.smo.shortener.urlservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/urls")
public class UrlServiceController {

    static final String SERVICE_API_V1 = "/api/v1/urls";

    private final UrlService urlService;

    @GetMapping("/{id}")
    public ResponseEntity<UrlData> getById(@PathVariable String id) {
        var url = urlService.getById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource with id: '" + id + "' not found!"));

        return ResponseEntity.ok(url);
    }

    @GetMapping("/short/{shortUrl}")
    public ResponseEntity<UrlData> getByShortUrl(@PathVariable String shortUrl) {
        var url = urlService.getByShortUrl(shortUrl)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource for shortUrl: '" + shortUrl + "' not found!"));

        return ResponseEntity.ok(url);
    }

    @GetMapping
    public ResponseEntity<Page<UrlData>> getUrls(
            @RequestParam(required = false) String user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (user != null && !user.isEmpty()) {
            return ResponseEntity.ok(urlService.getUrlsByUser(user, pageable));
        }

        return ResponseEntity.ok(urlService.getAllUrls(pageable));
    }

    @PostMapping
    public ResponseEntity<UrlData> create(@RequestBody @Validated UrlData url) {
        var savedUrl = urlService
                .saveUrl(url.getShortUrl(), url.getLongUrl(), url.getUser())
                .orElseThrow(() -> new UrlServiceException("Url not created!"));

        return new ResponseEntity<>(savedUrl, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UrlData> update(
            @PathVariable String id,
            @RequestBody @Valid UrlData url) {

        var updatedUrl = urlService
                .updateUrl(id, url.getShortUrl(), url.getLongUrl(), url.getStatus())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource with id: '" + id + "' not found!"));

        return ResponseEntity.ok(updatedUrl);
    }
}