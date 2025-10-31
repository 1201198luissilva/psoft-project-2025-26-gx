package pt.psoft.g1.psoftg1.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for database type and external APIs.
 */
@Configuration
@Getter
public class ProjectConfig {

    @Value("${db.type}")
    private String dbType;

    @Value("${external.api.ninjas.url}")
    private String ninjasApiUrl;

    @Value("${external.api.ninjas.key}")
    private String ninjasApiKey;

    @Value("${external.api.googlebooks.url}")
    private String googleBooksApiUrl;

    @Value("${external.api.googlebooks.key}")
    private String googleBooksApiKey;

    // Add more external API properties as needed
    // @Value("${external.api.another.url}")
    // private String anotherApiUrl;
}