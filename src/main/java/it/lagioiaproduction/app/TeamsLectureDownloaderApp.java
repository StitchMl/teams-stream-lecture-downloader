package it.lagioiaproduction.app;

import it.lagioiaproduction.core.DownloadCoordinator;
import it.lagioiaproduction.core.FileNameGenerator;
import it.lagioiaproduction.core.FfmpegRunner;
import it.lagioiaproduction.core.StreamLoginService;
import it.lagioiaproduction.core.StreamManifestResolver;
import it.lagioiaproduction.ui.MainFrame;
import it.lagioiaproduction.ui.theme.AppTheme;

import javax.swing.SwingUtilities;

public final class TeamsLectureDownloaderApp {
    private TeamsLectureDownloaderApp() {
    }

    public static void main(String[] args) {
        AppTheme.setup();

        StreamLoginService loginService = new StreamLoginService();
        StreamManifestResolver manifestResolver = new StreamManifestResolver();
        FileNameGenerator fileNameGenerator = new FileNameGenerator();
        FfmpegRunner ffmpegRunner = new FfmpegRunner();

        DownloadCoordinator coordinator = new DownloadCoordinator(
                loginService,
                manifestResolver,
                fileNameGenerator,
                ffmpegRunner
        );

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(coordinator);
            frame.setVisible(true);
        });
    }
}