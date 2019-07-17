package it.polito.ai.server.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonModule;

import java.util.Map;

import static java.lang.System.getenv;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

    @Bean
    public GeoJsonModule registerGeoJsonModule(){
        // needed to deserialize geoJson objects
        return new GeoJsonModule();
    }

    @Override
    public MongoClient mongoClient() {
        String host = System.getenv("hostname");
        if (host == null)
            host = "127.0.0.1";
        return new MongoClient(host, 27017);
    }

    @Override
    protected String getDatabaseName() {
        return "app";
    }

    public @Bean MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

}