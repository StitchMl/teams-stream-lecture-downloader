package it.lagioiaproduction.model;

import java.util.List;

public record DownloadSummary(
        int total,
        int successful,
        int failed,
        List<String> errors
) {
    public DownloadSummary {
        errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public boolean hasFailures() {
        return failed > 0;
    }

    public String errorsAsMultilineString() {
        if (errors.isEmpty()) {
            return "Nessun errore.";
        }
        return String.join(System.lineSeparator(), errors);
    }

    public String toHumanMessage() {
        return "Riepilogo finale -> completati: " + successful + ", falliti: " + failed;
    }
}