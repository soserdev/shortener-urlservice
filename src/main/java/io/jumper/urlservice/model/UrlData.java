package io.jumper.urlservice.model;

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

    public UrlData(String shortUrl, String longUrl, String userid) {
        this(null, shortUrl, longUrl, userid, LocalDateTime.now(), LocalDateTime.now());
    }

    @Id
    private String id;

    @NotBlank(message = "shortUrl is mandatory")
    @Indexed(unique = true)
    private String shortUrl;

    @NotBlank(message = "longUrl is mandatory")
    private String longUrl;

    @NotBlank(message = "userId is mandatory")
    private String userid;

    private LocalDateTime created;
    private LocalDateTime updated;
}
