package it.lagioiaproduction.ui.sections;

import it.lagioiaproduction.ui.components.RoundedPanel;
import it.lagioiaproduction.ui.theme.AppColors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.GridLayout;

public class StatsSection extends JPanel {
    private final JLabel sessionValueLabel = createStatValueLabel();

    private final JLabel urlsValueLabel = createStatValueLabel();

    private final JLabel parallelValueLabel = createStatValueLabel();

    public StatsSection() {
        setOpaque(false);
        setLayout(new GridLayout(1, 3, 16, 0));

        JLabel sessionSubLabel = createMutedLabel("");
        add(createCard("Sessione", sessionValueLabel, sessionSubLabel));
        JLabel urlsSubLabel = createMutedLabel("Link unici validi");
        add(createCard("URL in coda", urlsValueLabel, urlsSubLabel));
        JLabel parallelSubLabel = createMutedLabel("Numero di download simultanei");
        add(createCard("Paralleli", parallelValueLabel, parallelSubLabel));
    }

    public void setSessionSaved(boolean saved) {
        if (saved) {
            sessionValueLabel.setText("Connesso");
            sessionValueLabel.setForeground(AppColors.SUCCESS);
        } else {
            sessionValueLabel.setText("Assente");
            sessionValueLabel.setForeground(AppColors.WARNING);
        }
    }

    public void setUrlCount(int count) {
        urlsValueLabel.setText(String.valueOf(count));
        urlsValueLabel.setForeground(AppColors.TEXT_PRIMARY);
    }

    public void setParallelCount(int parallel) {
        parallelValueLabel.setText(String.valueOf(parallel));
        parallelValueLabel.setForeground(AppColors.TEXT_PRIMARY);
    }

    private static RoundedPanel createCard(String title, JLabel valueLabel, JLabel subLabel) {
        RoundedPanel card = new RoundedPanel(AppColors.CARD_BG, AppColors.BORDER, 24);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(AppColors.TEXT_SECONDARY);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 12f));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(subLabel);

        return card;
    }

    private static JLabel createStatValueLabel() {
        JLabel label = new JLabel("0");
        label.setForeground(AppColors.TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 26f));
        return label;
    }

    private static JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppColors.TEXT_SECONDARY);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        return label;
    }
}