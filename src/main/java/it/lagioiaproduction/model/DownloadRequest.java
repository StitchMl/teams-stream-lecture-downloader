package it.lagioiaproduction.model;

import java.nio.file.Path;

public record DownloadRequest(
        String streamUrl,
        Path outputDirectory,
        int index,
        int total
) {
}