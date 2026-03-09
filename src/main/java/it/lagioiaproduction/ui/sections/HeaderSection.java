package it.lagioiaproduction.ui.sections;

import it.lagioiaproduction.ui.components.BadgeLabel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HeaderSection extends JPanel {
    private final BadgeLabel sessionBadge;

    public HeaderSection() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 18, 10, 18));
        setPreferredSize(new Dimension(1000, 95));
        setMinimumSize(new Dimension(200, 95));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 6));
        contentPanel.setOpaque(false);

        JPanel topRow = new JPanel(new BorderLayout(12, 0));
        topRow.setOpaque(false);

        JLabel title = new JLabel("Teams Stream Lecture Downloader");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        sessionBadge = new BadgeLabel(
                "Sessione non salvata",
                new Color(255, 255, 255, 24),
                Color.WHITE
        );
        sessionBadge.setBorder(new EmptyBorder(6, 12, 6, 12));
        sessionBadge.setFont(sessionBadge.getFont().deriveFont(Font.BOLD, 11.5f));

        topRow.add(title, BorderLayout.WEST);
        topRow.add(sessionBadge, BorderLayout.EAST);

        JLabel subtitle = new JLabel("Scarica registrazioni in sola visualizzazione.");
        subtitle.setForeground(new Color(232, 240, 255));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 11.5f));

        contentPanel.add(topRow, BorderLayout.NORTH);
        contentPanel.add(subtitle, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    public void setSessionSaved(boolean saved) {
        if (saved) {
            sessionBadge.setBadgeText("Sessione attiva");
            sessionBadge.setBadgeColors(new Color(34, 197, 94, 42), Color.WHITE);
        } else {
            sessionBadge.setBadgeText("Sessione non salvata");
            sessionBadge.setBadgeColors(new Color(255, 255, 255, 24), Color.WHITE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(
                0, 0, new Color(79, 29, 149),
                getWidth(), getHeight(), new Color(6, 182, 212)
        );
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

        g2.setColor(new Color(255, 255, 255, 10));
        g2.fillOval(-35, 12, 95, 95);
        g2.fillOval(getWidth() - 120, -16, 120, 120);

        g2.dispose();
        super.paintComponent(g);
    }
}