package it.lagioiaproduction.core;

import it.lagioiaproduction.model.DownloadProgress;
import it.lagioiaproduction.model.DownloadRequest;
import it.lagioiaproduction.model.DownloadSummary;
import it.lagioiaproduction.model.ResolvedStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class DownloadCoordinator {
    private final StreamLoginService loginService;
    private final StreamManifestResolver manifestResolver;
    private final FileNameGenerator fileNameGenerator;
    private final FfmpegRunner ffmpegRunner;

    public DownloadCoordinator(
            StreamLoginService loginService,
            StreamManifestResolver manifestResolver,
            FileNameGenerator fileNameGenerator,
            FfmpegRunner ffmpegRunner
    ) {
        this.loginService = loginService;
        this.manifestResolver = manifestResolver;
        this.fileNameGenerator = fileNameGenerator;
        this.ffmpegRunner = ffmpegRunner;
    }

    public boolean hasAuthState() {
        return loginService.hasAuthState();
    }

    public void loginAndSaveState(Consumer<String> statusConsumer) throws Exception {
        loginService.loginAndSaveState(statusConsumer);
    }

    public DownloadSummary downloadMultiple(
            List<String> urls,
            Path outputDir,
            int maxParallel,
            Consumer<String> statusConsumer,
            Consumer<DownloadProgress> progressConsumer
    ) throws Exception {
        if (!loginService.hasAuthState()) {
            throw new IllegalStateException("Sessione non trovata. Premi prima 'Login Microsoft'.");
        }

        List<String> distinctUrls = urls == null
                ? List.of()
                : urls.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        if (distinctUrls.isEmpty()) {
            throw new IllegalStateException("Nessun URL da scaricare.");
        }

        Files.createDirectories(outputDir);

        int total = distinctUrls.size();
        int poolSize = Math.max(1, Math.min(maxParallel, total));

        notifyStatus(statusConsumer, "Download totali: " + total);
        notifyStatus(statusConsumer, "Download paralleli: " + poolSize);

        for (int i = 0; i < total; i++) {
            int index = i + 1;
            notifyProgress(progressConsumer, new DownloadProgress(
                    index,
                    total,
                    "File " + index,
                    "In coda",
                    0.0,
                    true,
                    "In attesa di uno slot libero..."
            ));
        }

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<Future<Void>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < total; i++) {
                int index = i + 1;
                String url = distinctUrls.get(i);

                DownloadRequest request = new DownloadRequest(url, outputDir, index, total);

                futures.add(executor.submit(() -> {
                    downloadSingle(request, statusConsumer, progressConsumer);
                    return null;
                }));
            }

            int success = 0;
            int failed = 0;
            List<String> errors = new ArrayList<>();

            for (int i = 0; i < futures.size(); i++) {
                try {
                    futures.get(i).get();
                    success++;
                } catch (ExecutionException ex) {
                    failed++;
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    errors.add("- File " + (i + 1) + ": " + rootCauseMessage(cause));
                }
            }

            return new DownloadSummary(total, success, failed, errors);
        } finally {
            executor.shutdownNow();
        }
    }

    public void downloadSingle(
            DownloadRequest request,
            Consumer<String> statusConsumer,
            Consumer<DownloadProgress> progressConsumer
    ) throws Exception {
        if (!loginService.hasAuthState()) {
            throw new IllegalStateException("Sessione non trovata. Premi prima 'Login Microsoft'.");
        }

        Files.createDirectories(request.outputDirectory());

        notifyStatus(statusConsumer, "Risoluzione link " + request.index() + "/" + request.total() + "...");
        notifyProgress(progressConsumer, new DownloadProgress(
                request.index(),
                request.total(),
                "File " + request.index(),
                "Risoluzione link",
                0.0,
                true,
                "Recupero embed e manifest DASH..."
        ));

        try {
            ResolvedStream resolved = manifestResolver.resolve(
                    request.streamUrl(),
                    loginService.getAuthStatePath(),
                    null
            );

            String baseFileName = fileNameGenerator.buildBaseFileName(
                    request.streamUrl(),
                    resolved.pageTitle()
            );

            Path outputFile = fileNameGenerator.reserveUniquePath(
                    request.outputDirectory(),
                    baseFileName,
                    ".mp4"
            );

            String displayName = outputFile.getFileName().toString();

            notifyStatus(statusConsumer, "Avvio download: " + displayName);
            notifyProgress(progressConsumer, new DownloadProgress(
                    request.index(),
                    request.total(),
                    displayName,
                    "Preparazione file",
                    0.0,
                    true,
                    "Connessione a ffmpeg..."
            ));

            try {
                ffmpegRunner.download(resolved.manifestUrl(), outputFile, update ->
                        notifyProgress(progressConsumer, new DownloadProgress(
                                request.index(),
                                request.total(),
                                displayName,
                                "Download in corso",
                                update.fraction(),
                                false,
                                update.detail()
                        )));
            } catch (Exception ex) {
                Files.deleteIfExists(outputFile);

                notifyProgress(progressConsumer, new DownloadProgress(
                        request.index(),
                        request.total(),
                        displayName,
                        "Errore",
                        0.0,
                        false,
                        rootCauseMessage(ex)
                ));

                throw ex;
            }

            notifyProgress(progressConsumer, new DownloadProgress(
                    request.index(),
                    request.total(),
                    displayName,
                    "Completato",
                    1.0,
                    false,
                    "File salvato in: " + outputFile.toAbsolutePath()
            ));

            notifyStatus(statusConsumer, "Completato: " + displayName);
        } catch (Exception ex) {
            notifyProgress(progressConsumer, new DownloadProgress(
                    request.index(),
                    request.total(),
                    "File " + request.index(),
                    "Errore",
                    0.0,
                    false,
                    rootCauseMessage(ex)
            ));
            throw ex;
        }
    }

    private String rootCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() != null ? current.getMessage() : current.toString();
    }

    private void notifyStatus(Consumer<String> statusConsumer, String message) {
        if (statusConsumer != null) {
            statusConsumer.accept(message);
        }
    }

    private void notifyProgress(Consumer<DownloadProgress> progressConsumer, DownloadProgress progress) {
        if (progressConsumer != null) {
            progressConsumer.accept(progress);
        }
    }
}