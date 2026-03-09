package it.lagioiaproduction.model;

public record ResolvedStream(
        String originalUrl,
        String pageTitle,
        String embedUrl,
        String manifestUrl
) {
}