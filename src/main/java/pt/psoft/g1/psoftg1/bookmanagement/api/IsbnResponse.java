package pt.psoft.g1.psoftg1.bookmanagement.api;

import lombok.Data;

/**
 * Response DTO for ISBN lookup by title.
 */
@Data
public class IsbnResponse {
    private String isbn;
    private String source; // "google_books" or "open_library"

    public IsbnResponse(String isbn, String source) {
        this.isbn = isbn;
        this.source = source;
    }
}