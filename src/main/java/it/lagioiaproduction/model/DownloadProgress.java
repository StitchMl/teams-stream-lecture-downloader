package it.lagioiaproduction.model;

public record DownloadProgress(
        int index,
        int total,
        String displayName,
        String status,
        double fraction,
        boolean indeterminate,
        String detail
) {
}