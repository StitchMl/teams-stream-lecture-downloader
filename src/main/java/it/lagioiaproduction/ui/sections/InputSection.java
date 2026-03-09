package it.lagioiaproduction.ui.sections;

import it.lagioiaproduction.ui.components.LinkItemPanel;
import it.lagioiaproduction.ui.components.ModernButton;
import it.lagioiaproduction.ui.components.RoundedPanel;
import it.lagioiaproduction.ui.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InputSection extends RoundedPanel {
    private static final int FIELD_HEIGHT = 44;
    private static final int LINKS_AREA_HEIGHT = 150;

    private final JPanel linksListPanel = new JPanel();
    private final JScrollPane linksScrollPane;

    private final List<LinkItemPanel> linkItems = new ArrayList<>();
    private final List<Runnable> urlsChangedListeners = new ArrayList<>();

    private final ModernButton addLinkButton = ModernButton.soft("+ Nuovo link");
    private final JTextField outputField = new JTextField(Paths.get("downloads").toAbsolutePath().toString());
    private final JSpinner parallelSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 5, 1));

    private final ModernButton browseButton = ModernButton.soft("Sfoglia");
    private final ModernButton loginButton = ModernButton.secondary("Login Microsoft");
    private final ModernButton downloadButton = ModernButton.primary("Scarica video");

    private final JLabel footerStatusLabel = new JLabel("Pronto per il download.");
    private final JProgressBar activityBar = new JProgressBar();

    public InputSection() {
        super(AppColors.CARD_BG, AppColors.BORDER, 28);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(18, 18, 18, 18));

        linksListPanel.setOpaque(false);
        linksListPanel.setLayout(new BoxLayout(linksListPanel, BoxLayout.Y_AXIS));

        linksScrollPane = new JScrollPane(linksListPanel);
        linksScrollPane.setBorder(null);
        linksScrollPane.setOpaque(false);
        linksScrollPane.getViewport().setOpaque(false);
        linksScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        linksScrollPane.setPreferredSize(new Dimension(0, LINKS_AREA_HEIGHT));
        linksScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(createHeading(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);

        addLinkItem();
    }

    private JComponent createHeading() {
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Nuovo download");
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JLabel subtitle = new JLabel("Aggiungi i link, scegli la cartella e avvia la coda di download.");
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12.5f));
        subtitle.setBorder(new EmptyBorder(4, 0, 0, 0));

        left.add(title);
        left.add(subtitle);

        addLinkButton.setPreferredSize(new Dimension(150, 40));

        heading.add(left, BorderLayout.WEST);
        heading.add(addLinkButton, BorderLayout.EAST);

        return heading;
    }

    private JComponent createBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(createLinksSection());
        body.add(Box.createVerticalStrut(14));
        body.add(createSettingsPanel());

        return body;
    }

    private JComponent createLinksSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = createSectionLabel("Registrazioni da scaricare");

        left.add(title);
        left.add(Box.createVerticalStrut(2));

        top.add(left, BorderLayout.WEST);

        RoundedPanel shell = new RoundedPanel(AppColors.PANEL_BG, new Color(255, 255, 255, 22), 24);
        shell.setLayout(new BorderLayout());
        shell.setBorder(new EmptyBorder(8, 8, 8, 8));
        shell.add(linksScrollPane, BorderLayout.CENTER);

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(shell, BorderLayout.CENTER);

        return wrapper;
    }

    private JComponent createSettingsPanel() {
        styleTextField(outputField);
        outputField.setMargin(new Insets(10, 12, 10, 12));
        outputField.putClientProperty("JTextField.showClearButton", true);

        parallelSpinner.setPreferredSize(new Dimension(120, FIELD_HEIGHT));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) parallelSpinner.getEditor();
        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 14, 14);
        panel.add(createFieldLabelBlock(
                "Cartella di destinazione",
                "Dove salvare i file scaricati."
        ), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(outputField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 14, 0);
        browseButton.setPreferredSize(new Dimension(300, FIELD_HEIGHT));
        panel.add(browseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 14);
        panel.add(createFieldLabelBlock(
                "Download simultanei",
                "Quanti file scaricare nello stesso momento."
        ), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        panel.add(parallelSpinner, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        loginButton.setPreferredSize(new Dimension(150, FIELD_HEIGHT));
        downloadButton.setPreferredSize(new Dimension(150, FIELD_HEIGHT));

        buttonsPanel.add(loginButton);
        buttonsPanel.add(downloadButton);

        gbc.gridx = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(buttonsPanel, gbc);

        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(14, 0, 0, 0));

        footerStatusLabel.setForeground(AppColors.TEXT_SECONDARY);
        footerStatusLabel.setFont(footerStatusLabel.getFont().deriveFont(Font.PLAIN, 12f));

        activityBar.setIndeterminate(false);
        activityBar.setVisible(false);
        activityBar.setBorderPainted(false);
        activityBar.setOpaque(false);
        activityBar.setPreferredSize(new Dimension(160, 10));

        footer.add(footerStatusLabel, BorderLayout.WEST);
        footer.add(activityBar, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.add(footer, BorderLayout.SOUTH);

        return wrapper;
    }

    private void addLinkItem() {
        LinkItemPanel item = new LinkItemPanel(linkItems.size() + 1);
        item.setUrl("");

        item.addRemoveListener(e -> removeLinkItem(item));
        item.addContentChangedListener(this::notifyUrlsChanged);

        linkItems.add(item);
        linksListPanel.add(item);
        linksListPanel.add(Box.createVerticalStrut(8));

        refreshLinkItemsUi();
        notifyUrlsChanged();
        revalidate();
        repaint();

        item.requestInputFocus();
    }

    private void removeLinkItem(LinkItemPanel item) {
        if (linkItems.size() <= 1) {
            item.setUrl("");
            notifyUrlsChanged();
            return;
        }

        int componentIndex = linksListPanel.getComponentZOrder(item);
        if (componentIndex >= 0) {
            linksListPanel.remove(componentIndex);
            if (componentIndex < linksListPanel.getComponentCount()) {
                Component next = linksListPanel.getComponent(componentIndex);
                if (next instanceof Box.Filler) {
                    linksListPanel.remove(next);
                }
            } else if (linksListPanel.getComponentCount() > 0) {
                Component last = linksListPanel.getComponent(linksListPanel.getComponentCount() - 1);
                if (last instanceof Box.Filler) {
                    linksListPanel.remove(last);
                }
            }
        }

        linkItems.remove(item);

        refreshLinkItemsUi();
        notifyUrlsChanged();
        revalidate();
        repaint();
    }

    private void refreshLinkItemsUi() {
        boolean removable = linkItems.size() > 1;

        for (int i = 0; i < linkItems.size(); i++) {
            LinkItemPanel item = linkItems.get(i);
            item.setIndex(i + 1);
            item.setRemoveVisible(removable);
        }
    }

    private void notifyUrlsChanged() {
        for (Runnable listener : urlsChangedListeners) {
            listener.run();
        }
    }

    public List<String> getUrls() {
        return linkItems.stream()
                .map(LinkItemPanel::getUrl)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    public Path getOutputDirectory() {
        String text = outputField.getText().trim();
        if (text.isBlank()) {
            return Paths.get("downloads");
        }
        return Paths.get(text);
    }

    public int getParallelDownloads() {
        return (Integer) parallelSpinner.getValue();
    }

    public void setBusy(boolean busy) {
        addLinkButton.setEnabled(!busy);
        outputField.setEnabled(!busy);
        parallelSpinner.setEnabled(!busy);
        browseButton.setEnabled(!busy);
        loginButton.setEnabled(!busy);
        downloadButton.setEnabled(!busy && !getUrls().isEmpty());

        for (LinkItemPanel item : linkItems) {
            item.setEditingEnabled(!busy);
            item.setRemoveVisible(!busy && linkItems.size() > 1);
        }
    }

    public void setFooterStatus(String text) {
        footerStatusLabel.setText(text);
    }

    public void setActivityVisible(boolean visible) {
        activityBar.setVisible(visible);
        activityBar.setIndeterminate(visible);
    }

    public void setDownloadButtonText(String text) {
        downloadButton.setText(text);
    }

    public void setDownloadEnabled(boolean enabled) {
        downloadButton.setEnabled(enabled);
    }

    public void setOutputDirectory(Path path) {
        outputField.setText(path.toAbsolutePath().toString());
    }

    public void addBrowseListener(ActionListener listener) {
        browseButton.addActionListener(listener);
    }

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addDownloadListener(ActionListener listener) {
        downloadButton.addActionListener(listener);
    }

    public void addUrlsChangedListener(Runnable listener) {
        urlsChangedListeners.add(listener);
    }

    public void addParallelChangedListener(Runnable listener) {
        parallelSpinner.addChangeListener(e -> listener.run());
    }

    public Path chooseDirectory(Component parent) {
        JFileChooser chooser = new JFileChooser(getOutputDirectory().toString());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            return chooser.getSelectedFile().toPath().toAbsolutePath();
        }
        return null;
    }

    public void addNewEmptyLink() {
        addLinkItem();
    }

    public void addAddLinkListener(ActionListener listener) {
        addLinkButton.addActionListener(listener);
    }

    private static JPanel createFieldLabelBlock(String title, String help) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = createSectionLabel(title);
        JLabel helpLabel = createHelperLabel(help);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(helpLabel);

        return panel;
    }

    private static JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppColors.TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        return label;
    }

    private static JLabel createHelperLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppColors.TEXT_SECONDARY);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 11.5f));
        return label;
    }

    private static void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
        field.setForeground(AppColors.TEXT_PRIMARY);
        field.setCaretColor(AppColors.CARET);
    }
}