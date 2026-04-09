package dev.smo.shortener.urlservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "urls")
@CompoundIndex(
        name = "domain_shorturl_idx",
        def = "{'domain': 1, 'shortUrl': 1}",
        unique = true
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlData {

    @Id
    private String id;

    @NotBlank(message = "domain is mandatory")
    @Indexed
    private String domain;

    @NotBlank(message = "shortUrl is mandatory")
    @Indexed
    private String shortUrl;

    @NotBlank(message = "longUrl is mandatory")
    private String longUrl;

    @NotBlank(message = "user is mandatory")
    private String user;

    private String status;
    private LocalDateTime created;
    private LocalDateTime updated;

    public UrlData(String domain, String shortUrl, String longUrl, String user) {
        this(
                null,
                normalizeDomain(domain),
                shortUrl,
                longUrl,
                user,
                UrlStatus.ACTIVE.toString(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private static String normalizeDomain(String domain) {
        if (domain == null) return null;

        // normalize:
        // - lowercase
        // - remove protocol
        // - remove trailing slash
        return domain
                .toLowerCase()
                .replace("http://", "")
                .replace("https://", "")
                .replaceAll("/+$", "");
    }
}