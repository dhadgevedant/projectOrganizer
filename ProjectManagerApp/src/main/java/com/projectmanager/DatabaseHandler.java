package com.projectmanager;

import com.projectmanager.model.Project;
import com.projectmanager.model.Task;
import com.projectmanager.model.TeamMember;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    

    // //LOCAL DB
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/project_manager";
    // private static final String USER = "root";
    // private static final String PASS = "";

    // Aiven DB
    private static final String DB_URL = "jdbc:mysql://" + "mysql-projectmanager-dhadge-projectmanager01.l.aivencloud.com" + ":" + "10382" + "/" + "project_manager" + "?sslmode=require";
    private static final String USER = "avnadmin";
    private static final String PASS = "";
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    // Projects CRUD operations
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT * FROM projects";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Project project = new Project(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                );
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return projects;
    }
    
    public void addProject(Project project) {
        String query = "INSERT INTO projects (name, description) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Team Members CRUD operations
    public List<TeamMember> getTeamMembersByProject(int projectId) {
        List<TeamMember> members = new ArrayList<>();
        String query = "SELECT * FROM team_members WHERE project_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TeamMember member = new TeamMember(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("project_id")
                );
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
    public void addTeamMember(TeamMember member) {
        String query = "INSERT INTO team_members (name, project_id) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, member.getName());
            pstmt.setInt(2, member.getProjectId());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteTeamMember(int memberId) {
        String query = "DELETE FROM team_members WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Tasks CRUD operations
    public List<Task> getTasksByProject(int projectId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE project_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Task task = new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("project_id"),
                    rs.getInt("assigned_to"),
                    rs.getBoolean("completed")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    public List<Task> getCompletedTasksByProject(int projectId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE project_id = ? AND completed = true";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Task task = new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("project_id"),
                    rs.getInt("assigned_to"),
                    true
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    public void addTask(Task task) {
        String query = "INSERT INTO tasks (title, description, project_id, assigned_to, completed) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setInt(3, task.getProjectId());
            
            if (task.getAssignedTo() > 0) {
                pstmt.setInt(4, task.getAssignedTo());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            pstmt.setBoolean(5, task.isCompleted());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateTask(Task task) {
        String query = "UPDATE tasks SET title = ?, description = ?, assigned_to = ?, completed = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            
            if (task.getAssignedTo() > 0) {
                pstmt.setInt(3, task.getAssignedTo());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            
            pstmt.setBoolean(4, task.isCompleted());
            pstmt.setInt(5, task.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateTaskAssignment(int taskId, int memberId) {
        String query = "UPDATE tasks SET assigned_to = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            if (memberId > 0) {
                pstmt.setInt(1, memberId);
            } else {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            }
            
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateTaskCompletion(int taskId, boolean completed) {
        String query = "UPDATE tasks SET completed = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setBoolean(1, completed);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteTask(int taskId) {
        String query = "DELETE FROM tasks WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}