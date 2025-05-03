package com.projectmanager;

import com.projectmanager.model.Project;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ProjectCellRenderer extends JPanel implements ListCellRenderer<Project> {
    private final JLabel projectName;
    private final JLabel projectDescription;
    private final Color ACCENT_COLOR = new Color(0, 0, 0);
    private final Color TEXT_COLOR = new Color(0, 0, 0);
    private final Color SIDEBAR_COLOR = new Color(0, 0, 0);

    public ProjectCellRenderer() {
        setLayout(new BorderLayout(5, 2));
        setBorder(new EmptyBorder(5, 10, 5, 10));
        setOpaque(false);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 2));
        contentPanel.setOpaque(false);

        projectName = new JLabel();
        projectName.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        projectName.setForeground(TEXT_COLOR);

        projectDescription = new JLabel();
        projectDescription.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
        projectDescription.setForeground(new Color(100, 100, 100));

        contentPanel.add(projectName, BorderLayout.NORTH);
        contentPanel.add(projectDescription, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Project> list, Project project, 
                                                int index, boolean isSelected, boolean cellHasFocus) {
        projectName.setText(project.getName());
        
        String description = project.getDescription();
        if (description != null && !description.trim().isEmpty()) {
            // Truncate description if too long
            if (description.length() > 40) {
                description = description.substring(0, 37) + "...";
            }
            projectDescription.setText(description);
            projectDescription.setVisible(true);
        } else {
            projectDescription.setVisible(false);
        }

        if (isSelected) {
            setBackground(ACCENT_COLOR);
            setBorder(new CompoundBorder(
                new EmptyBorder(3, 3, 3, 3),
                new LineBorder(ACCENT_COLOR, 1, true)
            ));
            projectName.setForeground(Color.WHITE);
            projectDescription.setForeground(new Color(230, 230, 230));
            setOpaque(true);
        } else {
            setBackground(SIDEBAR_COLOR);
            setBorder(new EmptyBorder(5, 10, 5, 10));
            projectName.setForeground(TEXT_COLOR);
            projectDescription.setForeground(new Color(100, 100, 100));
            setOpaque(false);
        }

        return this;
    }
}