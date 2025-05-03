package com.projectmanager.model;

public class Task {
    private int id;
    private String title;
    private String description;
    private int projectId;
    private int assignedTo; // 0 means unassigned
    private boolean completed; // New field for task completion status
    
    public Task(String title, String description, int projectId) {
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.assignedTo = 0;
        this.completed = false;
    }
    
    public Task(int id, String title, String description, int projectId, Integer assignedTo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.assignedTo = assignedTo != null ? assignedTo : 0;
        this.completed = false;
    }
    
    // Constructor that includes the completed field
    public Task(int id, String title, String description, int projectId, Integer assignedTo, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.assignedTo = assignedTo != null ? assignedTo : 0;
        this.completed = completed;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    public int getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    @Override
    public String toString() {
        return title;
    }
}