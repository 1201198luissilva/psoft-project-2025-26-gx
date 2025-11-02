package pt.psoft.g1.psoftg1.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for Open Library API.
 */
@Configuration
@RequiredArgsConstructor
public class OpenLibraryApiConfig {

    @Value("${external.api.openlib.url}")
    private String baseUrl;

    @Bean(name = "openLibraryWebClient")
    public WebClient openLibraryWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}