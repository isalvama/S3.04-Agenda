package org.agenda.task.model;

import java.time.LocalDateTime;

public class Task {

    private Long id;
    private String title;
    private String body;
    private Status status;
    private Priority priority;
    private LocalDateTime expirationDate;
    private final LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Long eventId;

    public Task(Long id, String title, String body, Status status) {}

}