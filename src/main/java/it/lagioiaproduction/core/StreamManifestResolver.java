package it.lagioiaproduction.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Request;
import it.lagioiaproduction.model.ResolvedStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamManifestResolver {
    private static final Pattern SRC_PATTERN =
            Pattern.compile("src=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

    private static final double MANIFEST_TIMEOUT_MS = 30_000;

    public ResolvedStream resolve(String streamUrl, Path authStatePath, Consumer<String> logger) {
        if (authStatePath == null || !Files.exists(authStatePath)) {
            throw new IllegalStateException("Sessione non trovata. Premi prima 'Login Microsoft'.");
        }

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(
                     new BrowserType.LaunchOptions().setHeadless(true)
             )) {

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions().setStorageStatePath(authStatePath)
            );

            log(logger, "Apro la pagina Stream...");
            Page streamPage = context.newPage();
            streamPage.navigate(streamUrl);
            streamPage.waitForTimeout(4000);

            log(logger, "Cerco il codice di incorporamento...");
            openEmbedDialog(streamPage);

            String iframeHtml = readEmbedTextarea(streamPage);
            String embedUrl = extractEmbedUrl(iframeHtml);
            log(logger, "Codice di incorporamento recuperato.");

            Page embedPage = context.newPage();

            log(logger, "Apro la pagina embed e intercetto il videomanifest...");
            Request manifestRequest = embedPage.waitForRequest(
                                request -> {
                                    String url = request.url();
                                    return url.contains("videomanifest?")
                                            && url.contains("format=dash");
                                },
                    new Page.WaitForRequestOptions().setTimeout(MANIFEST_TIMEOUT_MS),
                    () -> embedPage.navigate(embedUrl)
                        );

            String manifestUrl = trimAfterFormatDash(manifestRequest.url());
            log(logger, "Manifest DASH intercettato.");

            return new ResolvedStream(
                    streamUrl,
                    streamPage.title(),
                    embedUrl,
                    manifestUrl
            );
        }
    }

    private void openEmbedDialog(Page page) {
        clickFirst(page,
                "button:has-text(\"Condividi\")",
                "button:has-text(\"Share\")",
                "text=Condividi",
                "text=Share"
        );

        page.waitForTimeout(1000);

        clickFirst(page,
                "text=Codice di incorporamento",
                "text=Embed code",
                "text=Incorpora",
                "text=Embed"
        );

        page.waitForTimeout(1500);
    }

    private String readEmbedTextarea(Page page) {
        Locator textarea = page.locator("textarea").first();
        textarea.waitFor(new Locator.WaitForOptions().setTimeout(15000));

        String value = textarea.inputValue();
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Textarea embed non trovata o vuota.");
        }
        return value;
    }

    private String extractEmbedUrl(String iframeHtml) {
        Matcher matcher = SRC_PATTERN.matcher(iframeHtml);
        if (!matcher.find()) {
            throw new IllegalStateException("Impossibile estrarre src dall'iframe.");
        }
        return matcher.group(1).replace("&amp;", "&");
    }

    private String trimAfterFormatDash(String url) {
        String marker = "format=dash";
        int index = url.indexOf(marker);
        if (index == -1) {
            return url;
        }
        return url.substring(0, index + marker.length());
    }

    private void clickFirst(Page page, String... selectors) {
        PlaywrightException last = null;

        for (String selector : selectors) {
            try {
                Locator loc = page.locator(selector).first();
                if (loc.count() > 0) {
                    loc.click(new Locator.ClickOptions().setTimeout(5000));
                    return;
                }
            } catch (PlaywrightException e) {
                last = e;
            }
        }

        throw new IllegalStateException(
                "Nessun selettore ha funzionato: " + Arrays.toString(selectors),
                last
        );
    }

    private void log(Consumer<String> logger, String message) {
        if (logger != null) {
            logger.accept(message);
        }
    }
}