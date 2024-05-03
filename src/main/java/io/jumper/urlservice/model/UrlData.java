package io.jumper.urlservice.model;

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
public class UrlData {

    public UrlData(String shortUrl, String longUrl, String userid) {
        this(null, shortUrl, longUrl, userid, LocalDateTime.now(), LocalDateTime.now());
    }

    @Id
    private String id;

    @Indexed(unique = true)
    private String shortUrl;

    private String longUrl;

    private String userid;

    private LocalDateTime created;
    private LocalDateTime updated;
}
