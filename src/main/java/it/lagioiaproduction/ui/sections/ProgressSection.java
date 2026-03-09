package it.lagioiaproduction.ui.sections;

import it.lagioiaproduction.model.DownloadProgress;
import it.lagioiaproduction.ui.components.ProgressItemPanel;
import it.lagioiaproduction.ui.components.RoundedPanel;
import it.lagioiaproduction.ui.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProgressSection extends RoundedPanel {
    private final JPanel listPanel = new JPanel();
    private final JScrollPane scrollPane;
    private final JLabel subtitleLabel = new JLabel("Nessun download in corso.");

    private final Map<Integer, ProgressItemPanel> itemPanels = new LinkedHashMap<>();
    private int totalItems;

    public ProgressSection() {
        super(AppColors.CARD_BG, AppColors.BORDER, 28);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(22, 22, 22, 22));

        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(0, 280));

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    private JComponent createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Avanzamento download");
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        subtitleLabel.setForeground(AppColors.TEXT_SECONDARY);
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 13f));
        subtitleLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        header.add(title);
        header.add(subtitleLabel);

        return header;
    }

    private JComponent createBody() {
        RoundedPanel shell = new RoundedPanel(AppColors.PANEL_BG, new Color(255, 255, 255, 18), 24);
        shell.setLayout(new BorderLayout());
        shell.setBorder(new EmptyBorder(10, 10, 10, 10));
        shell.add(scrollPane, BorderLayout.CENTER);
        return shell;
    }

    public void prepareDownloads(int total) {
        SwingUtilities.invokeLater(() -> {
            totalItems = total;
            itemPanels.clear();
            listPanel.removeAll();

            if (total <= 0) {
                subtitleLabel.setText("Nessun download in corso.");
                revalidate();
                repaint();
                return;
            }

            subtitleLabel.setText("File in coda: " + total);

            for (int i = 1; i <= total; i++) {
                ProgressItemPanel panel = new ProgressItemPanel(i);
                panel.apply(new DownloadProgress(
                        i,
                        total,
                        "File " + i,
                        "In coda",
                        0.0,
                        true,
                        "In attesa di essere processato..."
                ));
                itemPanels.put(i, panel);
                listPanel.add(panel);
                if (i < total) {
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }

            revalidate();
            repaint();
        });
    }

    public void updateProgress(DownloadProgress progress) {
        SwingUtilities.invokeLater(() -> {
            ProgressItemPanel panel = itemPanels.get(progress.index());
            if (panel == null) {
                panel = new ProgressItemPanel(progress.index());
                itemPanels.put(progress.index(), panel);
                if (listPanel.getComponentCount() > 0) {
                    listPanel.add(Box.createVerticalStrut(10));
                }
                listPanel.add(panel);
            }

            panel.apply(progress);
            updateSubtitle();
            revalidate();
            repaint();
        });
    }

    private void updateSubtitle() {
        if (itemPanels.isEmpty()) {
            subtitleLabel.setText("Nessun download in corso.");
            return;
        }

        subtitleLabel.setText("File monitorati: " + Math.max(totalItems, itemPanels.size()));
    }

}