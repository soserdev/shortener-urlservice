package io.jumper.urlservice.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;


@Configuration
@ConfigurationProperties(prefix = "io.jumper.api.mongodb", ignoreUnknownFields = true)
@Setter
@Slf4j
public class MongodbConfig extends AbstractMongoClientConfiguration {

    private String host;
    private String database;

    @Bean
    @Override
    public MongoClient mongoClient() {
        return super.mongoClient();
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        log.info("Connecting to mongodb: '" + host + "'");
        ConnectionString connectionString = new ConnectionString(host);
        builder.applyConnectionString(connectionString);
    }

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

}

