package dev.smo.shortener.urlservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "urls")
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlData {

    @Id
    private String id;

    @NotBlank(message = "shortUrl is mandatory")
    @Indexed(unique = true)
    private String shortUrl;

    @NotBlank(message = "longUrl is mandatory")
    private String longUrl;

    @NotBlank(message = "user is mandatory")
    private String user;

    private String status;
    private LocalDateTime created;
    private LocalDateTime updated;

    public UrlData(String shortUrl, String longUrl, String user) {
        this(null, shortUrl, longUrl, user, UrlStatus.ACTIVE.toString(), LocalDateTime.now(), LocalDateTime.now());
    }
}
