package pt.psoft.g1.psoftg1.external.service;

import lombok.Data;

import java.util.List;

/**
 * Open Library API response DTO.
 */
@Data
public class OpenLibraryResponse {
    private int start;
    private int num_found;
    private List<OpenLibraryDoc> docs;

    @Data
    public static class OpenLibraryDoc {
        private String title;
        private List<String> isbn;
        private String key;
    }
}