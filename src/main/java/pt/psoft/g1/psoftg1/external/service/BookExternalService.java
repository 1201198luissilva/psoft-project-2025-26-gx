package pt.psoft.g1.psoftg1.external.service;

import java.util.Optional;

/**
 * Service for external book APIs.
 */
public interface BookExternalService {

    /**
     * Get ISBN by title using available external APIs.
     * Checks for Google Books API key first, then Open Library.
     *
     * @param title the book title
     * @return Optional containing the ISBN if found
     */
    Optional<String> getIsbnByTitle(String title);
}