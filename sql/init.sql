DROP DATABASE IF EXISTS agenda;
CREATE DATABASE agenda CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE agenda;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS note;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS event;

CREATE TABLE event (
    id           INT UNSIGNED    AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(500)    NOT NULL,
    body         TEXT,
    event_date   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    event_type   ENUM('BIRTHDATE','APPOINTMENT','REMINDER','OTHER')
                                 NOT NULL DEFAULT 'OTHER',
    repetition   VARCHAR(250),
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_event_date (event_date),
    INDEX idx_event_type (event_type)
) ENGINE = InnoDB;

CREATE TABLE task (
    id              INT UNSIGNED    AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(500)    NOT NULL,
    body            TEXT            NOT NULL,
    status_task     ENUM('PENDING','IN PROGRESS','DONE')
                                    NOT NULL DEFAULT 'PENDING',
    priority_type   ENUM('HIGH','MEDIUM','LOW'),
    expiration_date TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP,
    event_id        INT UNSIGNED,
    CONSTRAINT fk_task_event
        FOREIGN KEY (event_id) REFERENCES event (id)
        ON DELETE SET NULL,
    INDEX idx_status       (status_task),
    INDEX idx_priority_type (priority_type)
) ENGINE = InnoDB;

CREATE TABLE note (
    id         INT UNSIGNED    AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(500)    NOT NULL DEFAULT '',
    body       TEXT,
    created_at TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,
    task_id    INT UNSIGNED    NOT NULL,
    CONSTRAINT fk_note_task
        FOREIGN KEY (task_id) REFERENCES task (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

SET FOREIGN_KEY_CHECKS = 1;