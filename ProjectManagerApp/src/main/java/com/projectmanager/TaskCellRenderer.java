package com.projectmanager;

import com.projectmanager.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {
    private final JCheckBox checkbox;
    private final JLabel titleLabel;
    private final JLabel descriptionLabel;
    private final DatabaseHandler db;
    private final Color BACKGROUND_COLOR = new Color(250, 250, 252);
    private final Color SELECTED_COLOR = new Color(210, 230, 255);
    
    public TaskCellRenderer(DatabaseHandler db) {
        this.db = db;
        setLayout(new BorderLayout(5, 0));
        setOpaque(false);
        
        // Main content panel with some padding
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        

        contentPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        contentPanel.setOpaque(false);
        
        checkbox = new JCheckBox();
        checkbox.setOpaque(false);
        
        // Title with bold font
        titleLabel = new JLabel();
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, titleLabel.getFont().getSize()));
        
        // Description with single line and ellipsis if too long
        descriptionLabel = new JLabel();
        descriptionLabel.setFont(new Font(descriptionLabel.getFont().getName(), Font.PLAIN, descriptionLabel.getFont().getSize() - 1));
        descriptionLabel.setForeground(Color.GRAY);
        
        // Add components to content panel
        JPanel titlePanel = new JPanel(new BorderLayout(8, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(checkbox, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(4)); // Space between title and description
        contentPanel.add(descriptionLabel);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index, boolean isSelected, boolean cellHasFocus) {
        // Set task details
        titleLabel.setText(task.getTitle());
        checkbox.setSelected(task.isCompleted());
        
        // Prepare description - show just one line with ellipsis if needed
        String description = task.getDescription().trim();
        if (description.isEmpty()) {
            descriptionLabel.setText(" ");  // Empty space to maintain height
        } else {
            // Limit to first line and add ellipsis if there are more lines
            int newlineIndex = description.indexOf('\n');
            if (newlineIndex > 0) {
                description = description.substring(0, newlineIndex) + "...";
            } else if (description.length() > 60) {
                description = description.substring(0, 57) + "...";
            }
            descriptionLabel.setText(description);
        }
        
        // Handle selection state
        if (isSelected) {
            setBackground(SELECTED_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(list.getSelectionBackground().darker(), 1, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
            ));
        } else {
            setBackground(BACKGROUND_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
            ));
        }
        
        return this;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded rectangle background
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2d.dispose();
        
        super.paintComponent(g);
    }
}