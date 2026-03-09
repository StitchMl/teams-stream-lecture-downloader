package it.lagioiaproduction.ui.components;

import it.lagioiaproduction.model.DownloadProgress;
import it.lagioiaproduction.ui.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProgressItemPanel extends RoundedPanel {
    private final JLabel titleLabel = new JLabel("File 1");
    private final BadgeLabel statusBadge = new BadgeLabel(
            "In coda",
            new Color(255, 255, 255, 18),
            AppColors.TEXT_PRIMARY
    );
    private final JLabel detailLabel = new JLabel("In attesa...");
    private final JLabel percentLabel = new JLabel("—");
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    public ProgressItemPanel(int index) {
        super(AppColors.PANEL_BG, new Color(255, 255, 255, 18), 22);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        buildHeader(index);
        buildBody();
    }

    private void buildHeader(int index) {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        titleLabel.setText("File " + index);
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));

        percentLabel.setForeground(AppColors.TEXT_PRIMARY);
        percentLabel.setFont(percentLabel.getFont().deriveFont(Font.BOLD, 13f));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(titleLabel);
        left.add(statusBadge);

        header.add(left, BorderLayout.WEST);
        header.add(percentLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void buildBody() {
        detailLabel.setForeground(AppColors.TEXT_SECONDARY);
        detailLabel.setFont(detailLabel.getFont().deriveFont(Font.PLAIN, 12f));

        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 14));

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setOpaque(false);
        body.add(progressBar, BorderLayout.NORTH);
        body.add(detailLabel, BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);
    }

    public void apply(DownloadProgress progress) {
        titleLabel.setText(progress.displayName() == null || progress.displayName().isBlank()
                ? "File " + progress.index()
                : progress.displayName());

        detailLabel.setText(progress.detail() == null || progress.detail().isBlank()
                ? "—"
                : progress.detail());

        statusBadge.setBadgeText(progress.status());
        applyStatusColors(progress.status());

        progressBar.setIndeterminate(progress.indeterminate());

        if (!progress.indeterminate()) {
            int value = (int) Math.round(Math.max(0.0, Math.min(1.0, progress.fraction())) * 100.0);
            progressBar.setValue(value);
            percentLabel.setText(value + "%");
        } else {
            percentLabel.setText("...");
        }
    }

    private void applyStatusColors(String status) {
        if (status == null) {
            statusBadge.setBadgeColors(new Color(255, 255, 255, 18), AppColors.TEXT_PRIMARY);
            return;
        }

        String normalized = status.trim().toLowerCase();

        if (normalized.contains("errore")) {
            statusBadge.setBadgeColors(new Color(239, 68, 68, 45), Color.WHITE);
        } else if (normalized.contains("completato")) {
            statusBadge.setBadgeColors(new Color(34, 197, 94, 45), Color.WHITE);
        } else if (normalized.contains("download")) {
            statusBadge.setBadgeColors(new Color(59, 130, 246, 45), Color.WHITE);
        } else if (normalized.contains("risoluzione") || normalized.contains("preparazione")) {
            statusBadge.setBadgeColors(new Color(245, 158, 11, 45), Color.WHITE);
        } else {
            statusBadge.setBadgeColors(new Color(255, 255, 255, 18), AppColors.TEXT_PRIMARY);
        }
    }
}