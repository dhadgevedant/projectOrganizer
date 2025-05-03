package com.projectmanager;

import com.projectmanager.model.Task;
import com.projectmanager.model.TeamMember;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TaskEditorDialog extends JDialog {
    private final DatabaseHandler db;
    private final Task task;
    private final List<TeamMember> teamMembers;
    private boolean confirmed = false;
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<TeamMember> assigneeCombo;
    private JCheckBox completedCheck;
    
    public TaskEditorDialog(Frame owner, Task task, List<TeamMember> teamMembers, DatabaseHandler db) {
        super(owner, "Edit Task", true);
        this.task = task;
        this.teamMembers = teamMembers;
        this.db = db;
        
        setupUI();
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private void setupUI() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPanel);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title field
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        titleField = new JTextField(30);
        titleField.setText(task.getTitle());
        formPanel.add(titleField, gbc);
        
        // Description field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setText(task.getDescription());
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);
        
        // Assignee dropdown
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Assigned to:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        assigneeCombo = new JComboBox<>();
        assigneeCombo.addItem(new TeamMember("Unassigned", task.getProjectId()) {
            @Override
            public int getId() {
                return 0;
            }
            
            @Override
            public String toString() {
                return "Unassigned";
            }
        });
        
        for (TeamMember member : teamMembers) {
            assigneeCombo.addItem(member);
        }
        
        // Set the selected team member
        for (int i = 0; i < assigneeCombo.getItemCount(); i++) {
            if (assigneeCombo.getItemAt(i).getId() == task.getAssignedTo()) {
                assigneeCombo.setSelectedIndex(i);
                break;
            }
        }
        
        formPanel.add(assigneeCombo, gbc);
        
        // Completed checkbox
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        
        completedCheck = new JCheckBox("Mark as completed");
        completedCheck.setSelected(task.isCompleted());
        formPanel.add(completedCheck, gbc);
        
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            saveChanges();
            confirmed = true;
            dispose();
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);
        
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    private void saveChanges() {
        task.setTitle(titleField.getText().trim());
        task.setDescription(descriptionArea.getText().trim());
        task.setAssignedTo(((TeamMember) assigneeCombo.getSelectedItem()).getId());
        task.setCompleted(completedCheck.isSelected());
        
        db.updateTask(task);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}