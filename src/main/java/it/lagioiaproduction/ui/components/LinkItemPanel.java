package it.lagioiaproduction.ui.components;

import it.lagioiaproduction.ui.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class LinkItemPanel extends RoundedPanel {
    private final JLabel indexLabel = new JLabel("Link 1");
    private final BadgeLabel stateBadge = new BadgeLabel(
            "Vuoto",
            new Color(255, 255, 255, 20),
            AppColors.TEXT_PRIMARY
    );
    private final ModernButton removeButton = ModernButton.soft("Rimuovi");
    private final HintTextArea urlArea = new HintTextArea(
            "Incolla qui l'URL della registrazione Stream o SharePoint..."
    );

    public LinkItemPanel(int index) {
        super(AppColors.PANEL_BG, new Color(255, 255, 255, 18), 20);
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(6, 12, 6, 12));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        buildHeader(index);
        buildBody();
        bindInternalEvents();
        refreshState();
    }

    private void buildHeader(int index) {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        indexLabel.setForeground(AppColors.TEXT_PRIMARY);
        indexLabel.setFont(indexLabel.getFont().deriveFont(Font.BOLD, 14f));
        setIndex(index);

        left.add(indexLabel);
        left.add(stateBadge);

        header.add(left, BorderLayout.WEST);
        header.add(removeButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void buildBody() {
        urlArea.setRows(2);
        urlArea.setLineWrap(true);
        urlArea.setWrapStyleWord(true);
        urlArea.setForeground(AppColors.TEXT_PRIMARY);
        urlArea.setCaretColor(AppColors.CARET);
        urlArea.setBackground(AppColors.PANEL_BG);
        urlArea.setSelectionColor(AppColors.SELECTION);
        urlArea.setBorder(new EmptyBorder(6, 6, 6, 6));
        urlArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int) 13f));

        JScrollPane scrollPane = new JScrollPane(urlArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 52));

        JPanel body = new JPanel(new BorderLayout(0, 6));
        body.setOpaque(false);
        body.add(scrollPane, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
    }

    private void bindInternalEvents() {
        urlArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshState();
            }
        });
    }

    private void refreshState() {
        if (getUrl().isBlank()) {
            stateBadge.setBadgeText("Vuoto");
            stateBadge.setBadgeColors(new Color(255, 255, 255, 18), AppColors.TEXT_SECONDARY);
        } else {
            stateBadge.setBadgeText("Pronto");
            stateBadge.setBadgeColors(new Color(34, 197, 94, 45), Color.WHITE);
        }
    }

    public void setIndex(int index) {
        indexLabel.setText("Link " + index);
    }

    public String getUrl() {
        return urlArea.getText().trim();
    }

    public void setUrl(String url) {
        urlArea.setText(url == null ? "" : url);
        refreshState();
    }

    public void setRemoveVisible(boolean visible) {
        removeButton.setVisible(visible);
    }

    public void setEditingEnabled(boolean enabled) {
        urlArea.setEnabled(enabled);
        removeButton.setEnabled(enabled);
    }

    public void addRemoveListener(ActionListener listener) {
        removeButton.addActionListener(listener);
    }

    public void addContentChangedListener(Runnable listener) {
        urlArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                listener.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                listener.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                listener.run();
            }
        });
    }

    public void requestInputFocus() {
        SwingUtilities.invokeLater(urlArea::requestFocusInWindow);
    }
}