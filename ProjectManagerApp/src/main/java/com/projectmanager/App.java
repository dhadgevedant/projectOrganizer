package com.projectmanager;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ProjectManagerUI app = new ProjectManagerUI();
            app.setVisible(true);
        });
    }
}