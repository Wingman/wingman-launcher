package com.wingman.launcher.ui.components;

import com.wingman.launcher.ui.Colors;

import javax.swing.*;

public class DarkPanel extends JPanel {

    public DarkPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Colors.DARK_0F0F0F);
    }
}
