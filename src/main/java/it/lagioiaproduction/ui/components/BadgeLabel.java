package it.lagioiaproduction.ui.components;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class BadgeLabel extends JLabel {
    private Color backgroundColor;

    public BadgeLabel(String text, Color backgroundColor, Color foregroundColor) {
        super(text);
        this.backgroundColor = backgroundColor;

        setForeground(foregroundColor);
        setBorder(new EmptyBorder(8, 14, 8, 14));
        setFont(getFont().deriveFont(java.awt.Font.BOLD, 12f));
        setOpaque(false);
    }

    public void setBadgeText(String text) {
        setText(text);
    }

    public void setBadgeColors(Color backgroundColor, Color foregroundColor) {
        this.backgroundColor = backgroundColor;
        setForeground(foregroundColor);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 999, 999);

        g2.dispose();
        super.paintComponent(g);
    }
}