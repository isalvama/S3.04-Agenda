DROP DATABASE IF EXISTS agenda;
CREATE DATABASE agenda CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE agenda;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS note;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS event;

CREATE TABLE event (
    id           INT UNSIGNED    AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(100)    NOT NULL,
    body         TEXT,
    date  		 TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type   		 ENUM('BIRTHDATE','APPOINTMENT','REMINDER','OTHER')
                                 NOT NULL DEFAULT 'OTHER',
    schedule     ENUM('@yearly','@monthly','@weekly','@daily', '@hourly'),
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_event_date (date),
    INDEX idx_event_type (type)
) ENGINE = InnoDB;

CREATE TABLE task (
    id              INT UNSIGNED    AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(100)    NOT NULL,
    body            TEXT,
    status     		ENUM('PENDING','IN PROGRESS','DONE')
                                    NOT NULL DEFAULT 'PENDING',
    priority   		ENUM('HIGH','MEDIUM','LOW'),
    expiration_date TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP,
    event_id        INT UNSIGNED,
    CONSTRAINT fk_task_event
        FOREIGN KEY (event_id) REFERENCES event (id)
        ON DELETE SET NULL,
    INDEX idx_task_status       (status),
    INDEX idx_task_priority 	(priority)
) ENGINE = InnoDB;

CREATE TABLE note (
    id         INT UNSIGNED    AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(100)    NOT NULL,
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