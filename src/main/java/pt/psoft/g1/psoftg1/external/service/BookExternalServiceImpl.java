package pt.psoft.g1.psoftg1.external.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pt.psoft.g1.psoftg1.configuration.ProjectConfig;

import java.util.Optional;

/**
 * Service for external book APIs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookExternalServiceImpl implements BookExternalService {

    private final WebClient googleBooksWebClient;
    private final WebClient openLibraryWebClient;
    private final ProjectConfig projectConfig;

    @Override
    public Optional<String> getIsbnByTitle(String title) {
        log.info("ISBN lookup requested for title: {}", title);

        if(!projectConfig.isExternalApiEnabled()) {
            log.info("External API calls are disabled in configuration");
            return Optional.empty();
        }
        // Try Google Books first if API key is available
        if (projectConfig.getGoogleBooksApiKey() != null && !projectConfig.getGoogleBooksApiKey().isEmpty()) {
            try {
                Optional<String> isbn = getIsbnFromGoogleBooks(title);
                if (isbn.isPresent()) {
                    log.info("Found ISBN via Google Books for '{}': {}", title, isbn.get());
                    return isbn;
                } else {
                    log.info("Google Books returned no ISBN for title: {}", title);
                }
            } catch (Exception e) {
                log.warn("Google Books lookup failed for '{}': {}", title, e.getMessage());
            }
        } else {
            log.info("Google Books API key not configured, skipping Google Books lookup");
        }

        // Fallback to Open Library
        try {
            Optional<String> result = getIsbnFromOpenLibrary(title);
            log.info("Open Library lookup result for '{}': {}", title, result.orElse("Not found"));
            return result;
        } catch (Exception e) {
            log.warn("Open Library lookup failed for '{}': {}", title, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> getIsbnFromGoogleBooks(String title) {
        try {
            GoogleBooksResponse response = googleBooksWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/volumes")
                            .queryParam("q", "intitle:" + title)
                            .queryParam("key", projectConfig.getGoogleBooksApiKey())
                            .queryParam("maxResults", 1)
                            .build())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                // Log 401 specifically to help diagnose auth issues
                                if (clientResponse.statusCode().value() == 401) {
                                    log.warn("Google Books API returned 401 Unauthorized for title: {}", title);
                                } else {
                                    log.warn("Google Books API returned error status: {} for title: {}", clientResponse.statusCode(), title);
                                }
                                return clientResponse.createException();
                            })
                    .bodyToMono(GoogleBooksResponse.class)
                    .block();

            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                GoogleBooksResponse.GoogleBooksItem item = response.getItems().get(0);
                if (item.getVolumeInfo() != null && item.getVolumeInfo().getIndustryIdentifiers() != null) {
                    return item.getVolumeInfo().getIndustryIdentifiers().stream()
                            .filter(id -> "ISBN_13".equals(id.getType()) || "ISBN_10".equals(id.getType()))
                            .findFirst()
                            .map(GoogleBooksResponse.GoogleBooksItem.VolumeInfo.IndustryIdentifier::getIdentifier);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get ISBN from Google Books API: {}", e.getMessage());
        }

        return Optional.empty();
    }

    private Optional<String> getIsbnFromOpenLibrary(String title) {
        try {
            OpenLibraryResponse response = openLibraryWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search.json")
                            .queryParam("title", title)
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                log.warn("Open Library API returned error status: {}", clientResponse.statusCode());
                                return clientResponse.createException();
                            })
                    .bodyToMono(OpenLibraryResponse.class)
                    .block();

            if (response != null && response.getDocs() != null && !response.getDocs().isEmpty()) {
                OpenLibraryResponse.OpenLibraryDoc doc = response.getDocs().get(0);
                if (doc.getIsbn() != null && !doc.getIsbn().isEmpty()) {
                    // Prefer ISBN-13, fallback to ISBN-10
                    return doc.getIsbn().stream()
                            .filter(isbn -> isbn.length() == 13)
                            .findFirst()
                            .or(() -> doc.getIsbn().stream().findFirst());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get ISBN from Open Library API: {}", e.getMessage());
        }

        return Optional.empty();
    }
}