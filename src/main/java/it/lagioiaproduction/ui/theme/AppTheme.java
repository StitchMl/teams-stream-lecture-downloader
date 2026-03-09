package it.lagioiaproduction.ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.UIManager;

public final class AppTheme {
    private AppTheme() {
    }

    public static void setup() {
        FlatDarkLaf.setup();

        UIManager.put("Component.arc", 18);
        UIManager.put("Button.arc", 18);
        UIManager.put("TextComponent.arc", 18);
        UIManager.put("ProgressBar.arc", 18);
        UIManager.put("Component.arrowType", "chevron");
        UIManager.put("TextComponent.selectAllOnFocusPolicy", "always");
    }
}