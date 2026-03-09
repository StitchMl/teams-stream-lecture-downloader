package it.lagioiaproduction.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class StreamLoginService {
    private static final Path AUTH_STATE = Paths.get("playwright", ".auth", "state.json");
    private static final double LOGIN_TIMEOUT_MS = 300_000;

    public boolean hasAuthState() {
        return Files.exists(AUTH_STATE);
    }

    public Path getAuthStatePath() {
        return AUTH_STATE;
    }

    public void loginAndSaveState(Consumer<String> logger) throws Exception {
        Files.createDirectories(AUTH_STATE.getParent());

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(
                     new BrowserType.LaunchOptions()
                             .setHeadless(false)
                             .setSlowMo(150)
             )) {

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            log(logger, "Apro il browser per il login Microsoft...");
            page.navigate("https://login.microsoftonline.com/");
            log(logger, "Completa login nel browser.");

            waitUntilLoginCompleted(page);

            log(logger, "Login rilevato, salvo la sessione...");
            context.storageState(new BrowserContext.StorageStateOptions().setPath(AUTH_STATE));
            log(logger, "Sessione salvata in: " + AUTH_STATE.toAbsolutePath());
        }
    }

    private void waitUntilLoginCompleted(Page page) {
        page.waitForURL(
                url -> url != null && !url.contains("login.microsoftonline.com"),
                new Page.WaitForURLOptions().setTimeout(LOGIN_TIMEOUT_MS)
        );
    }

    private void log(Consumer<String> logger, String message) {
        if (logger != null) {
            logger.accept(message);
        }
    }
}