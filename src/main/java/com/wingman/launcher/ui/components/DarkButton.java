package com.wingman.launcher.ui.components;

import com.wingman.launcher.ui.Colors;

import javax.swing.*;
import java.awt.*;

public class DarkButton extends JButton {

    public DarkButton(String label) {
        this();
        this.setText(label);
    }

    public DarkButton() {
        this.setForeground(Colors.LIGHT_EEEEEE);
        this.setBackground(Colors.DARK_282828);
        this.setFont(new Font("Arial", Font.PLAIN, 14));
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
