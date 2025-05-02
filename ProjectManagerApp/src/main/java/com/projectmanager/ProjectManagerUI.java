package com.projectmanager;

import com.projectmanager.model.Project;
import com.projectmanager.model.Task;
import com.projectmanager.model.TeamMember;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        
        contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);
        
        // Left sidebar with projects
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        JLabel projectsHeader = new JLabel("Projects");
        projectsHeader.setFont(new Font("Arial", Font.BOLD, 18));
        projectsHeader.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        sidebarPanel.add(projectsHeader, BorderLayout.NORTH);
        
        projectListModel = new DefaultListModel<>();
        projectList = new JList<>(projectListModel);
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                return c;
            }
        });
        
        projectList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && projectList.getSelectedValue() != null) {
                openProject(projectList.getSelectedValue());
            }
        });
        
        JScrollPane projectScrollPane = new JScrollPane(projectList);
        projectScrollPane.setBorder(BorderFactory.createEmptyBorder());
        sidebarPanel.add(projectScrollPane, BorderLayout.CENTER);
        
        JButton addProjectBtn = new JButton("New Project");
        addProjectBtn.addActionListener(e -> addNewProject());
        sidebarPanel.add(addProjectBtn, BorderLayout.SOUTH);
        
        contentPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Main content area
        projectPanel = new JPanel(new BorderLayout());
        projectPanel.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(projectPanel, BorderLayout.CENTER);
        
        // Initial welcome message
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        JLabel welcomeLabel = new JLabel("Select or create a project to get started");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomePanel.add(welcomeLabel);
        projectPanel.add(welcomePanel, BorderLayout.CENTER);
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
        
        // Project header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel projectTitle = new JLabel(project.getName());
        projectTitle.setFont(new Font("Arial", Font.BOLD, 24));
        projectTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerPanel.add(projectTitle, BorderLayout.WEST);
        
        projectPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content with tasks and team members
        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        
        // Tasks section (left side)
        tasksPanel = new JPanel(new BorderLayout());
        tasksPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        JPanel tasksHeader = new JPanel(new BorderLayout());
        JLabel tasksTitle = new JLabel("Tasks");
        tasksTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tasksHeader.add(tasksTitle, BorderLayout.WEST);
        
        JButton addTaskBtn = new JButton("Add Task");
        addTaskBtn.addActionListener(e -> addNewTask());
        tasksHeader.add(addTaskBtn, BorderLayout.EAST);
        
        tasksPanel.add(tasksHeader, BorderLayout.NORTH);
        
        // Unassigned tasks list with drag support
        JPanel unassignedTasksPanel = createTasksPanel();
        tasksPanel.add(unassignedTasksPanel, BorderLayout.CENTER);
        
        mainContent.add(tasksPanel, BorderLayout.CENTER);
        
        // Team members section (right side)
        teamMembersPanel = new JPanel(new BorderLayout());
        
        JPanel teamHeader = new JPanel(new BorderLayout());
        JLabel teamTitle = new JLabel("Team Members");
        teamTitle.setFont(new Font("Arial", Font.BOLD, 18));
        teamHeader.add(teamTitle, BorderLayout.WEST);
        
        JButton addMemberBtn = new JButton("Add Member");
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
        panel.setBorder(BorderFactory.createTitledBorder("Unassigned Tasks"));
        
        DefaultListModel<Task> tasksModel = new DefaultListModel<>();
        JList<Task> tasksList = new JList<>(tasksModel);
        
        // Load unassigned tasks
        List<Task> tasks = db.getTasksByProject(currentProject.getId());
        for (Task task : tasks) {
            if (task.getAssignedTo() == 0) {
                tasksModel.addElement(task);
            }
        }
        
        // Setup drag support
        tasksList.setDragEnabled(true);
        tasksList.setTransferHandler(new TaskTransferHandler());
        
        JScrollPane scrollPane = new JScrollPane(tasksList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTeamMembersGrid() {
        List<TeamMember> members = db.getTeamMembersByProject(currentProject.getId());
        
        JPanel grid = new JPanel(new GridLayout(1, Math.max(1, members.size())));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        if (members.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            JLabel emptyLabel = new JLabel("No team members yet");
            emptyLabel.setHorizontalAlignment(JLabel.CENTER);
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
            BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel nameLabel = new JLabel(member.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        column.add(nameLabel, BorderLayout.NORTH);
        
        DefaultListModel<Task> tasksModel = new DefaultListModel<>();
        
        // Load assigned tasks
        List<Task> tasks = db.getTasksByProject(currentProject.getId());
        for (Task task : tasks) {
            if (task.getAssignedTo() == member.getId()) {
                tasksModel.addElement(task);
            }
        }
        
        JList<Task> memberTasks = new JList<>(tasksModel);
        
        // Setup drop support
        memberTasks.setTransferHandler(new TaskTransferHandler(member.getId()));
        memberTasks.setDropMode(DropMode.ON);
        
        JScrollPane scrollPane = new JScrollPane(memberTasks);
        column.add(scrollPane, BorderLayout.CENTER);
        
        return column;
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
            return COPY;
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
            // Only allow dropping on team member columns
            return memberId > 0 && support.isDataFlavorSupported(taskFlavor);
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
    }
}