package pt.psoft.g1.psoftg1.external.service;

import lombok.Data;

import java.util.List;

/**
 * Google Books API response DTO.
 */
@Data
public class GoogleBooksResponse {
    private String kind;
    private List<GoogleBooksItem> items;

    @Data
    public static class GoogleBooksItem {
        private String kind;
        private String id;
        private VolumeInfo volumeInfo;

        @Data
        public static class VolumeInfo {
            private String title;
            private List<IndustryIdentifier> industryIdentifiers;

            @Data
            public static class IndustryIdentifier {
                private String type;
                private String identifier;
            }
        }
    }
}