package com.wingman.launcher.settings;

import com.wingman.launcher.ui.Colors;
import com.wingman.launcher.ui.components.DarkCheckBox;
import com.wingman.launcher.ui.components.DarkPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsScreen extends JDialog {

    private Settings settings;

    private DarkPanel panel = new DarkPanel();

    private final Border paddingBorder
            = BorderFactory.createEmptyBorder(10, 10, 10, 10);

    public SettingsScreen(Settings settings) {
        super();
        this.settings = settings;

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        addSetting("Allow downloading default plugins",
                Settings.DOWNLOAD_DEFAULT_PLUGINS,
                settings.getBoolean(Settings.DOWNLOAD_DEFAULT_PLUGINS));

        this.add(panel);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void addSetting(String description, final String key, Object currentValue) {
        DarkPanel horizontalPanel = new DarkPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setForeground(Colors.LIGHT_EEEEEE);
        descriptionLabel.setBorder(paddingBorder);

        horizontalPanel.add(descriptionLabel);
        horizontalPanel.add(Box.createHorizontalGlue());

        JComponent valueComponent = null;

        if (currentValue instanceof Boolean) {
            final DarkCheckBox checkBox = new DarkCheckBox();
            checkBox.setSelected((boolean) currentValue);
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    settings.updateBoolean(key, checkBox.isSelected());
                }
            });
            valueComponent = checkBox;
        }

        if (valueComponent != null) {
            valueComponent.setBorder(paddingBorder);
            horizontalPanel.add(valueComponent);
        }

        panel.add(horizontalPanel);
    }
}
