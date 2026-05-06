package com.agenda.note.model;

public class Note {
    private int id;
    private int taskId;
    private String content;

    public Note() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    }