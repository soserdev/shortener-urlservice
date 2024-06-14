package io.jumper.urlservice.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import static java.util.Collections.singletonList;

/*
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb", ignoreUnknownFields = true)
@Setter
*/
@Slf4j
public class MongoConfig extends AbstractMongoClientConfiguration {

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private String authenticationdatabase;

//    private String uri;

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    public String getDatabaseName() {
        return database;
    }

    /*
    public @Bean com.mongodb.client.MongoClient mongoClient() {
        return com.mongodb.client.MongoClients.create("mongodb://root:rootpw@127.0.0.1:27017");
    }
*/
    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {

        builder.credential(MongoCredential.createCredential(username, database, password.toCharArray()))
                .applyToClusterSettings(settings  -> {
                    settings.hosts(singletonList(new ServerAddress(host, Integer.parseInt(port))));
                });
    }

}

