package com.projectmanager;

import com.projectmanager.model.Project;
import com.projectmanager.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class CompletedTasksDialog extends JDialog {
    private final DatabaseHandler db;
    private final Project project;
    private JList<Task> completedTasksList;
    private DefaultListModel<Task> tasksModel;
    
    public CompletedTasksDialog(Frame owner, Project project, DatabaseHandler db) {
        super(owner, "Completed Tasks - " + project.getName(), true);
        this.project = project;
        this.db = db;
        
        setupUI();
        loadCompletedTasks();
        
        setSize(600, 400);
        setLocationRelativeTo(owner);
    }
    
    private void setupUI() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPanel);
        
        // Header
        JLabel headerLabel = new JLabel("Completed Tasks for " + project.getName());
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        contentPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Tasks list
        tasksModel = new DefaultListModel<>();
        completedTasksList = new JList<>(tasksModel);
        completedTasksList.setCellRenderer(new CompletedTaskRenderer());
        
        JScrollPane scrollPane = new JScrollPane(completedTasksList);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton reopenButton = new JButton("Reopen Task");
        reopenButton.addActionListener(e -> reopenSelectedTask());
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        buttonsPanel.add(reopenButton);
        buttonsPanel.add(closeButton);
        
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    private void loadCompletedTasks() {
        tasksModel.clear();
        List<Task> completedTasks = db.getCompletedTasksByProject(project.getId());
        
        for (Task task : completedTasks) {
            tasksModel.addElement(task);
        }
        
        if (completedTasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No completed tasks for this project.", 
                "No Completed Tasks", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void reopenSelectedTask() {
        Task selectedTask = completedTasksList.getSelectedValue();
        if (selectedTask != null) {
            selectedTask.setCompleted(false);
            db.updateTaskCompletion(selectedTask.getId(), false);
            loadCompletedTasks(); // Refresh the list
            
            JOptionPane.showMessageDialog(this, 
                "Task has been reopened.", 
                "Task Reopened", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a task to reopen.", 
                "No Task Selected", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // Custom renderer for completed tasks
    private static class CompletedTaskRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Task task = (Task) value;
            setText(task.getTitle());
            setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
            
            if (!isSelected) {
                setForeground(Color.GRAY);
            }
            
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            return c;
        }
    }
}