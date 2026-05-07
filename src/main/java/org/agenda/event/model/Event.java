package org.agenda.event.model;

public class Event {
    private int id;
    private String description;
    private String repetition;

    public Event() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRepetition() { return repetition; }
    public void setRepetition(String repetition) { this.repetition = repetition; }
}