package it.lagioiaproduction.ui.components;

import it.lagioiaproduction.ui.theme.AppColors;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    private final Color normalColor;
    private final Color hoverColor;
    private final Color pressedColor;

    private boolean hovered;
    private boolean pressed;

    public ModernButton(String text, Color normalColor, Color hoverColor, Color pressedColor, Color foreground) {
        super(text);
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.pressedColor = pressedColor;

        setForeground(foreground);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(11, 18, 11, 18));
        setFont(getFont().deriveFont(java.awt.Font.BOLD, 13f));
        putClientProperty("JButton.buttonType", "roundRect");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    public static ModernButton primary(String text) {
        return new ModernButton(
                text,
                AppColors.PRIMARY,
                AppColors.PRIMARY_HOVER,
                AppColors.PRIMARY_PRESSED,
                Color.WHITE
        );
    }

    public static ModernButton secondary(String text) {
        return new ModernButton(
                text,
                AppColors.SECONDARY,
                AppColors.SECONDARY_HOVER,
                AppColors.SECONDARY_PRESSED,
                Color.WHITE
        );
    }

    public static ModernButton soft(String text) {
        return new ModernButton(
                text,
                AppColors.SOFT,
                AppColors.SOFT_HOVER,
                AppColors.SOFT_PRESSED,
                AppColors.TEXT_PRIMARY
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = normalColor;
        if (!isEnabled()) {
            fill = normalColor.darker();
        } else if (pressed) {
            fill = pressedColor;
        } else if (hovered) {
            fill = hoverColor;
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

        g2.dispose();
        super.paintComponent(g);
    }
}