package com.projectmanager.model;

public class Task {
    private int id;
    private String title;
    private String description;
    private int projectId;
    private int assignedTo; // 0 means unassigned
    
    public Task(String title, String description, int projectId) {
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.assignedTo = 0;
    }
    
    public Task(int id, String title, String description, int projectId, Integer assignedTo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.assignedTo = assignedTo != null ? assignedTo : 0;
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
    
    public String getDescription() {
        return description;
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
    
    @Override
    public String toString() {
        return title;
    }
}