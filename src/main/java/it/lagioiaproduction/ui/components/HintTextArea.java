package it.lagioiaproduction.ui.components;

import it.lagioiaproduction.ui.theme.AppColors;

import javax.swing.JTextArea;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

public class HintTextArea extends JTextArea {
    private final String hint;

    public HintTextArea(String hint) {
        this.hint = hint;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!getText().isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(AppColors.TEXT_SECONDARY);
        g2.setFont(getFont());

        Insets insets = getInsets();
        FontMetrics fm = g2.getFontMetrics();

        int x = insets.left;
        int y = insets.top + fm.getAscent();

        for (String line : hint.split("\n")) {
            g2.drawString(line, x, y);
            y += fm.getHeight();
        }

        g2.dispose();
    }
}