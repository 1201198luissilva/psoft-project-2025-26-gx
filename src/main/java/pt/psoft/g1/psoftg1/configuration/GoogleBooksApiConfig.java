package pt.psoft.g1.psoftg1.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for Google Books API.
 */
@Configuration
@EnableConfigurationProperties
@RequiredArgsConstructor
public class GoogleBooksApiConfig {

    @Value("${external.api.googlebooks.url}")
    private String baseUrl;

    @Value("${external.api.googlebooks.key}")
    private String apiKey;

    @Bean(name = "googleBooksWebClient")
    public WebClient googleBooksWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}