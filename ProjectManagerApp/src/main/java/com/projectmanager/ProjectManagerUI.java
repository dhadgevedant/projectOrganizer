package com.projectmanager;

import com.projectmanager.model.Project;
import com.projectmanager.model.Task;
import com.projectmanager.model.TeamMember;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProjectManagerUI extends JFrame {
    private final DatabaseHandler db;
    private JPanel contentPanel;
    private JList<Project> projectList;
    private DefaultListModel<Project> projectListModel;
    private JPanel projectPanel;
    private JPanel teamMembersPanel;
    private JPanel tasksPanel;
    private Project currentProject;
    
    // Apple-inspired colors
    private final Color BACKGROUND_COLOR = new Color(248, 248, 250);
    private final Color SIDEBAR_COLOR = new Color(242, 242, 247);
    private final Color ACCENT_COLOR = new Color(0, 0, 0);
    private final Color BUTTON_COLOR = new Color(0, 122, 255);
    private final Color TEXT_COLOR = new Color(50, 50, 50);
    private final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private final Color CARD_BORDER = new Color(230, 230, 235);
    private final Color CARD_SHADOW = new Color(0, 0, 0, 10);
    
    public ProjectManagerUI() {
        db = new DatabaseHandler();
        setupUI();
        loadProjects();
    }
    
    private void setupUI() {
        setTitle("Project Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Set background color
        contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPanel);
        
        // Left sidebar with projects
        JPanel sidebarPanel = new JPanel(new BorderLayout(0, 10));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBorder(createRoundedBorder(10, 10, 10, 10));
        
        JLabel projectsHeader = new JLabel("Projects");
        projectsHeader.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        projectsHeader.setForeground(TEXT_COLOR);
        projectsHeader.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        sidebarPanel.add(projectsHeader, BorderLayout.NORTH);
        
        projectListModel = new DefaultListModel<>();
        projectList = new JList<>(projectListModel);
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectList.setBackground(SIDEBAR_COLOR);
        projectList.setBorder(null);
        projectList.setCellRenderer(new ProjectCellRenderer());
        
        projectList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && projectList.getSelectedValue() != null) {
                openProject(projectList.getSelectedValue());
            }
        });
        
        JScrollPane projectScrollPane = new JScrollPane(projectList);
        projectScrollPane.setBorder(null);
        projectScrollPane.setBackground(SIDEBAR_COLOR);
        projectScrollPane.getViewport().setBackground(SIDEBAR_COLOR);
        sidebarPanel.add(projectScrollPane, BorderLayout.CENTER);
        
        JButton addProjectBtn = createStyledButton("New Project");
        addProjectBtn.setToolTipText("Create a new project");
        addProjectBtn.setForeground(TEXT_COLOR);
        //addProjectBtn.setIcon(new ImageIcon(getClass().getResource("/icons/add.png")));
        addProjectBtn.addActionListener(e -> addNewProject());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(SIDEBAR_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        buttonPanel.add(addProjectBtn);
        sidebarPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Main content area
        projectPanel = new JPanel(new BorderLayout());
        projectPanel.setBackground(BACKGROUND_COLOR);
        projectPanel.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(projectPanel, BorderLayout.CENTER);
        
        // Initial welcome message
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(BACKGROUND_COLOR);
        JLabel welcomeLabel = new JLabel("Select or create a project to get started");
        welcomeLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(120, 120, 120));
        welcomePanel.add(welcomeLabel);
        projectPanel.add(welcomePanel, BorderLayout.CENTER);
    }
    
    // Custom Cell Renderer for Project List
    private class ProjectCellRenderer extends JPanel implements ListCellRenderer<Project> {
        private final JLabel projectName;
        private final JLabel projectDescription;

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
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        button.setMargin(new Insets(8, 15, 8, 15));
        
        // Make button rounded
        button.setBorder(new LineBorder(BUTTON_COLOR, 1, true));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 36));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }
    
    private CompoundBorder createRoundedBorder(int top, int left, int bottom, int right) {
        return new CompoundBorder(
                new LineBorder(new Color(230, 230, 235), 1, true),
                new EmptyBorder(top, left, bottom, right)
        );
    }
    
    private void loadProjects() {
        projectListModel.clear();
        List<Project> projects = db.getAllProjects();
        for (Project project : projects) {
            projectListModel.addElement(project);
        }
    }
    
    private void addNewProject() {
        String name = JOptionPane.showInputDialog(this, "Project Name:");
        if (name != null && !name.trim().isEmpty()) {
            String description = JOptionPane.showInputDialog(this, "Project Description (optional):");
            Project project = new Project(name, description != null ? description : "");
            db.addProject(project);
            loadProjects();
            
            // Select the newly added project
            for (int i = 0; i < projectListModel.size(); i++) {
                if (projectListModel.get(i).getId() == project.getId()) {
                    projectList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void openProject(Project project) {
        currentProject = project;
        projectPanel.removeAll();
        
        // Project header panel with gradient background
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel projectTitle = new JLabel(project.getName());
        projectTitle.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        projectTitle.setForeground(TEXT_COLOR);
        headerPanel.add(projectTitle, BorderLayout.WEST);

        // Add a button to view completed tasks
        JButton viewCompletedBtn = createStyledButton("View Completed Tasks");
        viewCompletedBtn.setToolTipText("View completed tasks for this project");
        viewCompletedBtn.setForeground(TEXT_COLOR);
        viewCompletedBtn.addActionListener(e -> openCompletedTasksDialog());
        headerPanel.add(viewCompletedBtn, BorderLayout.EAST);
        
        projectPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content with tasks and team members
        JPanel mainContent = new JPanel(new BorderLayout(20, 0));
        mainContent.setBackground(BACKGROUND_COLOR);
        
        // Tasks section (left side)
        tasksPanel = new JPanel(new BorderLayout());
        tasksPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel tasksHeader = new JPanel(new BorderLayout());
        tasksHeader.setBackground(BACKGROUND_COLOR);
        tasksHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel tasksTitle = new JLabel("Tasks");
        tasksTitle.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        tasksTitle.setForeground(TEXT_COLOR);
        tasksHeader.add(tasksTitle, BorderLayout.WEST);
        
        JButton addTaskBtn = createStyledButton("Add Task");
        addTaskBtn.setToolTipText("Add a new task");
        addTaskBtn.setForeground(TEXT_COLOR);
        addTaskBtn.addActionListener(e -> addNewTask());
        tasksHeader.add(addTaskBtn, BorderLayout.EAST);
        
        tasksPanel.add(tasksHeader, BorderLayout.NORTH);
        
        // Unassigned tasks list with drag support
        JPanel unassignedTasksPanel = createTasksPanel();
        tasksPanel.add(unassignedTasksPanel, BorderLayout.CENTER);
        
        mainContent.add(tasksPanel, BorderLayout.CENTER);
        
        // Team members section (right side)
        teamMembersPanel = new JPanel(new BorderLayout());
        teamMembersPanel.setPreferredSize(new Dimension(800, getHeight()));
        teamMembersPanel.setMaximumSize(new Dimension(250, getHeight()));
        // teamMembersPanel.setBorder(createRoundedBorder(15, 15, 15, 15));
        teamMembersPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel teamHeader = new JPanel(new BorderLayout());
        teamHeader.setBackground(BACKGROUND_COLOR);
        teamHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel teamTitle = new JLabel("Team Members");
        teamTitle.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        teamTitle.setForeground(TEXT_COLOR);
        teamHeader.add(teamTitle, BorderLayout.WEST);
        
        JButton addMemberBtn = createStyledButton("Add Member");
        addMemberBtn.setToolTipText("Add a new team member");
        addMemberBtn.setForeground(TEXT_COLOR);
        addMemberBtn.addActionListener(e -> addTeamMember());
        teamHeader.add(addMemberBtn, BorderLayout.EAST);
        
        teamMembersPanel.add(teamHeader, BorderLayout.NORTH);
        
        // Team members grid with columns for each member
        JPanel membersGrid = createTeamMembersGrid();
        teamMembersPanel.add(membersGrid, BorderLayout.CENTER);
        
        mainContent.add(teamMembersPanel, BorderLayout.EAST);
        
        projectPanel.add(mainContent, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createRoundedBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Unassigned Tasks");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        DefaultListModel<Task> tasksModel = new DefaultListModel<>();
        JList<Task> tasksList = new JList<>(tasksModel);
        tasksList.setCellRenderer(new TaskCardRenderer(db));
        tasksList.setFixedCellHeight(85); // Fixed height for card-like appearance
        
        // Load unassigned tasks
        List<Task> tasks = db.getTasksByProject(currentProject.getId());
        for (Task task : tasks) {
            if (task.getAssignedTo() == 0 && !task.isCompleted()) {
                tasksModel.addElement(task);
            }
        }
        
        // Setup drag support
        tasksList.setDragEnabled(true);
        tasksList.setTransferHandler(new TaskTransferHandler());
        
        // Add right-click (context) menu for tasks
        tasksList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 || (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown())) {
                    int index = tasksList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        tasksList.setSelectedIndex(index);
                        showTaskContextMenu(tasksList, e.getX(), e.getY(), tasksList.getSelectedValue(), null);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    // Double-click to edit
                    Task selectedTask = tasksList.getSelectedValue();
                    if (selectedTask != null) {
                        editTask(selectedTask);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tasksList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showTaskContextMenu(JComponent component, int x, int y, Task task, TeamMember assignedTo) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        
        JMenuItem editItem = new JMenuItem("Edit Task");
        editItem.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        editItem.addActionListener(e -> editTask(task));
        
        JMenuItem deleteItem = new JMenuItem("Delete Task");
        deleteItem.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        deleteItem.addActionListener(e -> deleteTask(task));
        
        JCheckBoxMenuItem completeItem = new JCheckBoxMenuItem("Mark as Completed");
        completeItem.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        completeItem.setSelected(task.isCompleted());
        completeItem.addActionListener(e -> {
            task.setCompleted(completeItem.isSelected());
            db.updateTaskCompletion(task.getId(), completeItem.isSelected());
            openProject(currentProject); // Refresh view
        });
        
        menu.add(editItem);
        menu.add(deleteItem);
        menu.addSeparator();
        menu.add(completeItem);
        
        // Add unassign option if task is assigned
        if (assignedTo != null) {
            menu.addSeparator();
            JMenuItem unassignItem = new JMenuItem("Unassign from " + assignedTo.getName());
            unassignItem.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
            unassignItem.addActionListener(e -> {
                db.updateTaskAssignment(task.getId(), 0);
                openProject(currentProject);
            });
            menu.add(unassignItem);
        }
        
        menu.show(component, x, y);
    }
    
    private void editTask(Task task) {
        List<TeamMember> teamMembers = db.getTeamMembersByProject(currentProject.getId());
        TaskEditorDialog dialog = new TaskEditorDialog(this, task, teamMembers, db);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            openProject(currentProject); // Refresh view
        }
    }

    private void deleteTask(Task task) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this task?\n" + task.getTitle(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            db.deleteTask(task.getId());
            openProject(currentProject); // Refresh view
        }
    }
    
    private void openCompletedTasksDialog() {
        CompletedTasksDialog dialog = new CompletedTasksDialog(this, currentProject, db);
        dialog.setVisible(true);
        openProject(currentProject); // Refresh view after dialog closes
    }
    
    private JPanel createTeamMembersGrid() {
        List<TeamMember> members = db.getTeamMembersByProject(currentProject.getId());
        
        JPanel grid = new JPanel(new GridLayout(1, Math.max(1, members.size())));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        if (members.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBorder(createRoundedBorder(15, 15, 15, 15));
            emptyPanel.setBackground(Color.WHITE);
            JLabel emptyLabel = new JLabel("No team members yet");
            emptyLabel.setHorizontalAlignment(JLabel.CENTER);
            emptyLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            grid.add(emptyPanel);
        } else {
            for (TeamMember member : members) {
                grid.add(createMemberColumn(member));
            }
        }
        
        return grid;
    }
    
    private JPanel createMemberColumn(TeamMember member) {
        JPanel column = new JPanel(new BorderLayout());
        column.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 235)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        column.setBackground(BACKGROUND_COLOR);
        
        // Header with member name and remove button
        JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel nameLabel = new JLabel(member.getName());
        nameLabel.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JButton removeButton = new JButton("×");
        removeButton.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        removeButton.setMargin(new Insets(0, 5, 0, 5));
        removeButton.setToolTipText("Remove team member");
        removeButton.addActionListener(e -> removeTeamMember(member));
        
        headerPanel.add(nameLabel, BorderLayout.CENTER);
        headerPanel.add(removeButton, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        column.add(headerPanel, BorderLayout.NORTH);
        
        // Tasks area with rounded border
        JPanel tasksArea = new JPanel(new BorderLayout());
        tasksArea.setBorder(createRoundedBorder(10, 10, 10, 10));
        tasksArea.setBackground(Color.WHITE);
        
        DefaultListModel<Task> tasksModel = new DefaultListModel<>();
        
        // Load assigned tasks that are not completed
        List<Task> tasks = db.getTasksByProject(currentProject.getId());
        for (Task task : tasks) {
            if (task.getAssignedTo() == member.getId() && !task.isCompleted()) {
                tasksModel.addElement(task);
            }
        }
        
        JList<Task> memberTasks = new JList<>(tasksModel);
        memberTasks.setCellRenderer(new TaskCardRenderer(db));
        memberTasks.setFixedCellHeight(85); // Match the height in createTasksPanel
        memberTasks.setBackground(Color.WHITE);
        
        // Setup drop support
        memberTasks.setTransferHandler(new TaskTransferHandler(member.getId()));
        memberTasks.setDropMode(DropMode.ON);
        
        // Add right-click menu for tasks
        memberTasks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 || (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown())) {
                    int index = memberTasks.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        memberTasks.setSelectedIndex(index);
                        showTaskContextMenu(memberTasks, e.getX(), e.getY(), memberTasks.getSelectedValue(), member);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    // Double-click to edit
                    Task selectedTask = memberTasks.getSelectedValue();
                    if (selectedTask != null) {
                        editTask(selectedTask);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(memberTasks);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tasksArea.add(scrollPane, BorderLayout.CENTER);
        
        column.add(tasksArea, BorderLayout.CENTER);
        
        return column;
    }
    
    private void removeTeamMember(TeamMember member) {
        // Check if member has tasks assigned
        List<Task> tasks = db.getTasksByProject(currentProject.getId());
        boolean hasAssignedTasks = tasks.stream()
                .anyMatch(task -> task.getAssignedTo() == member.getId());
        
        if (hasAssignedTasks) {
            int option = JOptionPane.showConfirmDialog(this, 
                    "This team member has assigned tasks. " +
                    "Would you like to unassign these tasks before removing the member?",
                    "Member Has Tasks",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (option == JOptionPane.CANCEL_OPTION) {
                return;
            } else if (option == JOptionPane.YES_OPTION) {
                // Unassign all tasks
                for (Task task : tasks) {
                    if (task.getAssignedTo() == member.getId()) {
                        db.updateTaskAssignment(task.getId(), 0);
                    }
                }
            }
        }
        
        // Remove the member
        db.deleteTeamMember(member.getId());
        openProject(currentProject); // Refresh view
    }
    
    private void addNewTask() {
        String title = JOptionPane.showInputDialog(this, "Task Title:");
        if (title != null && !title.trim().isEmpty()) {
            String description = JOptionPane.showInputDialog(this, "Task Description (optional):");
            Task task = new Task(title, description != null ? description : "", currentProject.getId());
            db.addTask(task);
            openProject(currentProject); // Refresh the view
        }
    }
    
    private void addTeamMember() {
        String name = JOptionPane.showInputDialog(this, "Team Member Name:");
        if (name != null && !name.trim().isEmpty()) {
            TeamMember member = new TeamMember(name, currentProject.getId());
            db.addTeamMember(member);
            openProject(currentProject); // Refresh the view
        }
    }
    
    // New custom renderer for task cards
    private class TaskCardRenderer extends JPanel implements ListCellRenderer<Task> {
        private final JLabel titleLabel;
        private final JLabel descriptionLabel;
        private final JPanel priorityIndicator;
        private final DatabaseHandler db;
        
        public TaskCardRenderer(DatabaseHandler db) {
            this.db = db;
            setLayout(new BorderLayout(8, 4));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setOpaque(false);
            
            JPanel cardPanel = new JPanel(new BorderLayout(8, 4));
            cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            cardPanel.setBackground(CARD_BACKGROUND);
            
            // Left side - priority indicator
            priorityIndicator = new JPanel();
            priorityIndicator.setPreferredSize(new Dimension(5, 0));
            priorityIndicator.setOpaque(true);
            cardPanel.add(priorityIndicator, BorderLayout.WEST);
            
            // Center - task content
            JPanel contentPanel = new JPanel(new BorderLayout(2, 4));
            contentPanel.setOpaque(false);
            
            titleLabel = new JLabel();
            titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 14));
            titleLabel.setForeground(TEXT_COLOR);
            
            descriptionLabel = new JLabel();
            descriptionLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
            descriptionLabel.setForeground(new Color(120, 120, 120));
            
            contentPanel.add(titleLabel, BorderLayout.NORTH);
            contentPanel.add(descriptionLabel, BorderLayout.CENTER);
            
            cardPanel.add(contentPanel, BorderLayout.CENTER);
            add(cardPanel, BorderLayout.CENTER);
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends Task> list, Task task, 
                                                       int index, boolean isSelected, boolean cellHasFocus) {
            titleLabel.setText(task.getTitle());
            
            // Show first line of description
            String description = task.getDescription();
            if (description != null && !description.isEmpty()) {
                // Get first line or truncate if too long
                int newlineIndex = description.indexOf('\n');
                String firstLine;
                if (newlineIndex > 0) {
                    firstLine = description.substring(0, newlineIndex);
                } else {
                    firstLine = description;
                }
                
                // Truncate if too long and add ellipsis
                if (firstLine.length() > 60) {
                    firstLine = firstLine.substring(0, 57) + "...";
                }
                
                descriptionLabel.setText(firstLine);
                descriptionLabel.setVisible(true);
            } else {
                descriptionLabel.setVisible(false);
            }
            
            // Set priority color (placeholder, you might want to add priority to your Task model)
            priorityIndicator.setBackground(getPriorityColor(null)); // Assuming no priority for now
            
            if (isSelected) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(3, 3, 3, 3),
                    BorderFactory.createLineBorder(ACCENT_COLOR, 2, true)
                ));
            } else {
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            }
            
            return this;
        }
        
       // Helper method to get color based on priority
        private Color getPriorityColor(String priorityString) {
            if (priorityString == null || priorityString.isEmpty()) {
                return ACCENT_COLOR; // Default color if no priority is set
            }
            
            try {
                enum Priority {
                    HIGH, MEDIUM, LOW
                }

                Priority priority = Priority.valueOf(priorityString.toUpperCase());
                switch (priority) {
                    case HIGH: return new Color(255, 59, 48); // Red
                    case MEDIUM: return new Color(255, 149, 0); // Orange
                    case LOW: return new Color(52, 199, 89); // Green
                }
            } catch (IllegalArgumentException e) {
                // Handle invalid priority string (log it, or use default)
            }
            return ACCENT_COLOR; // Default color for invalid or missing priority
        }
    }
    
    // Custom TransferHandler for drag and drop tasks
    private class TaskTransferHandler extends TransferHandler {
        private final DataFlavor taskFlavor = new DataFlavor(Task.class, "Task");
        private final int memberId;
        
        // Constructor for unassigned tasks list (source)
        public TaskTransferHandler() {
            this.memberId = 0;
        }
        
        // Constructor for team member column (target)
        public TaskTransferHandler(int memberId) {
            this.memberId = memberId;
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            final Task task = ((JList<Task>)c).getSelectedValue();
            
            return new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{taskFlavor};
                }
                
                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return flavor.equals(taskFlavor);
                }
                
                @Override
                public Object getTransferData(DataFlavor flavor) {
                    return task;
                }
            };
        }
        
        @Override
        public boolean canImport(TransferSupport support) {
            // Allow dropping on team member columns or unassigned list
            return support.isDataFlavorSupported(taskFlavor);
        }
        
        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            
            try {
                Task task = (Task) support.getTransferable().getTransferData(taskFlavor);
                db.updateTaskAssignment(task.getId(), memberId);
                openProject(currentProject); // Refresh view
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            // This is called after a successful drag and drop
            // The task's assignment has already been updated in importData
            super.exportDone(source, data, action);
        }
    }
}