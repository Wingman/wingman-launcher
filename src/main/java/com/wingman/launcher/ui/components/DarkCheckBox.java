package com.wingman.launcher.ui.components;

import com.wingman.launcher.Util;
import com.wingman.launcher.ui.Colors;

import javax.swing.*;
import java.io.IOException;

public class DarkCheckBox extends JCheckBox {

    public DarkCheckBox() {
        this.setBackground(Colors.DARK_0F0F0F);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        try {
            this.setIcon(new ImageIcon(Util.getFileAsBytes("icons/unchecked.png")));
            this.setSelectedIcon(new ImageIcon(Util.getFileAsBytes("icons/checked.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
