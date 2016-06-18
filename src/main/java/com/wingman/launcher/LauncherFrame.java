package com.wingman.launcher;

import com.google.common.base.Throwables;
import com.wingman.launcher.settings.SettingsScreen;
import com.wingman.launcher.ui.Colors;
import com.wingman.launcher.ui.components.DarkButton;
import com.wingman.launcher.ui.components.DarkPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class LauncherFrame extends JFrame {

    public SettingsScreen settingsScreen;

    public DarkPanel framePanel;
    public DarkButton launchButton;

    public LauncherFrame() {
        this.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));

        framePanel = new DarkPanel();
        framePanel.add(Box.createVerticalGlue());
        try {
            JLabel loadingImage = new JLabel(new ImageIcon(Util.getFileAsBytes("loading.png")));
            loadingImage.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            framePanel.add(loadingImage);
            framePanel.add(Box.createVerticalGlue());
        } catch (IOException e) {
            Throwables.propagate(e);
        }

        launchButton = new DarkButton("Waiting..");
        launchButton.setEnabled(false);
        launchButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (launchButton.getModel().isPressed()) {
                    launchButton.setForeground(Colors.DARK_282828);
                } else {
                    launchButton.setForeground(Colors.LIGHT_EEEEEE);
                }
            }
        });
        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchButton.setEnabled(false);
                if (launchClient()) {
                    System.exit(0);
                } else {
                    launchButton.setEnabled(true);
                    setButtonText("Failed to launch the client");
                }
            }
        });

        DarkButton settingsButton = new DarkButton();
        try {
            settingsButton.setIcon(new ImageIcon(Util.getFileAsBytes("icons/settings.png")));
        } catch (IOException e) {
            e.printStackTrace();
            settingsButton.setText("Settings");
        }
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsScreen.setVisible(!settingsScreen.isVisible());
            }
        });

        framePanel.add(launchButton);
        framePanel.add(Box.createVerticalGlue());
        framePanel.add(settingsButton);
        framePanel.add(Box.createVerticalGlue());

        this.setContentPane(framePanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(new Dimension(400, 250));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void setButtonText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                launchButton.setText(text);
            }
        });
    }

    public abstract boolean launchClient();
}
