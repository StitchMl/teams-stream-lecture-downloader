package it.lagioiaproduction.core;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameGenerator {
    private static final Pattern SITE_PATTERN =
            Pattern.compile("/sites/([^/]+)/", Pattern.CASE_INSENSITIVE);

    private static final Pattern LESSON_NUMBER_PATTERN =
            Pattern.compile("(?i)(?:lecture|lezione)\\s*[#_\\- ]*(\\d+)");

    private static final Set<String> STOP_WORDS = Set.of(
            "AND", "OF", "THE", "DI", "DE", "DEL", "DELLA", "DEI", "E"
    );

    private final Object fileNameLock = new Object();

    public String buildBaseFileName(String streamUrl, String pageTitle) {
        String courseCode = courseCodeFromUrl(streamUrl);
        String lessonLabel = lessonLabelFromTitle(pageTitle);
        return normalizeForFileName(courseCode + " - " + lessonLabel);
    }

    public Path reserveUniquePath(Path outputDir, String baseFileName, String extension) throws IOException {
        Files.createDirectories(outputDir);

        String normalizedExtension = extension.startsWith(".") ? extension : "." + extension;
        Path desiredPath = outputDir.resolve(baseFileName + normalizedExtension);

        synchronized (fileNameLock) {
            if (!Files.exists(desiredPath)) {
                Files.createFile(desiredPath);
                return desiredPath;
            }

            for (int i = 2; i < 1000; i++) {
                Path candidate = outputDir.resolve(baseFileName + " (" + i + ")" + normalizedExtension);
                if (!Files.exists(candidate)) {
                    Files.createFile(candidate);
                    return candidate;
                }
            }
        }

        throw new IllegalStateException("Troppi file con lo stesso nome.");
    }

    private String courseCodeFromUrl(String streamUrl) {
        Matcher matcher = SITE_PATTERN.matcher(streamUrl);
        if (!matcher.find()) {
            return "COURSE";
        }

        String slug = URLDecoder.decode(matcher.group(1), StandardCharsets.UTF_8);
        String normalized = slug.replace('_', '-');
        String[] tokens = normalized.split("-");

        StringBuilder acronym = getStringBuilder(tokens);

        return acronym.isEmpty() ? "COURSE" : acronym.toString();
    }

    private static StringBuilder getStringBuilder(String[] tokens) {
        int start = 0;
        if (tokens.length >= 2 && tokens[1].matches("\\d+")) {
            start = 2;
        }

        StringBuilder acronym = new StringBuilder();

        for (int i = start; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isBlank() || token.matches("\\d+")) {
                continue;
            }

            String upper = token.toUpperCase();
            if (STOP_WORDS.contains(upper)) {
                continue;
            }

            acronym.append(upper.charAt(0));
        }
        return acronym;
    }

    private String lessonLabelFromTitle(String title) {
        if (title == null || title.isBlank()) {
            return "Lecture";
        }

        Matcher matcher = LESSON_NUMBER_PATTERN.matcher(title);
        if (matcher.find()) {
            return "Lecture " + Integer.parseInt(matcher.group(1));
        }

        return "Lecture";
    }

    private String normalizeForFileName(String input) {
        String cleaned = input
                .replaceAll("[\\\\/:*?\"<>|]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return cleaned.isBlank() ? "video" : cleaned;
    }
}