package com.projectmanager.model;

public class TeamMember {
    private int id;
    private String name;
    private int projectId;
    
    public TeamMember(String name, int projectId) {
        this.name = name;
        this.projectId = projectId;
    }
    
    public TeamMember(int id, String name, int projectId) {
        this.id = id;
        this.name = name;
        this.projectId = projectId;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    @Override
    public String toString() {
        return name;
    }
}