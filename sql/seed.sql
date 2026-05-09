USE agenda;

-- ── Events ────────────────────────────────────────────────────────────
INSERT INTO event (title, body, date, type, schedule) VALUES
('Ana''s Birthday',
 'Buy a gift and book a restaurant',
 '2026-06-15 09:00:00',
 'BIRTHDATE',
 'YEARLY'),

('Annual Medical Checkup',
 'Bring last year''s blood test results',
 '2026-05-20 10:30:00',
 'APPOINTMENT',
 NULL),

('Sprint 3 Team Meeting',
 'Review CLI-Agenda project progress and plan next sprint',
 '2026-05-12 09:00:00',
 'REMINDER',
 'WEEKLY'),

('Pay Rent',
 'Bank transfer before the 5th of each month',
 '2026-06-05 09:00:00',
 'REMINDER',
 'MONTHLY'),

('Technology Conference',
 'Register before June 1st to get early bird discount',
 '2026-07-10 09:00:00',
 'OTHER',
 NULL);

-- ── Tasks ─────────────────────────────────────────────────────────────
INSERT INTO task (title, body, status, priority, expiration_date, event_id) VALUES
('Buy birthday gift',
 'Look for something related to her hobbies: books or painting supplies',
 'PENDING',
 'HIGH',
 '2026-06-14 23:59:00',
 1),

('Book restaurant',
 'Call the Italian restaurant on Main Street, table for 6 people',
 'IN PROGRESS',
 'HIGH',
 '2026-06-10 23:59:00',
 1),

('Prepare blood test',
 'Go on an empty stomach, book appointment at the health centre in advance',
 'PENDING',
 'MEDIUM',
 '2026-05-18 08:00:00',
 2),

('Prepare sprint presentation',
 'Include architecture diagram and CLI commands demo',
 'IN PROGRESS',
 'HIGH',
 '2026-05-11 23:59:00',
 3),

('Review pending pull requests',
 'Review docker-setup and project-structure PRs before the meeting',
 'PENDING',
 'MEDIUM',
 '2026-05-11 12:00:00',
 3),

('Make rent transfer',
 'Amount: 850 euros. Reference: rent June 2026',
 'PENDING',
 'HIGH',
 '2026-06-05 09:00:00',
 4),

('Register for the conference',
 'Use team discount code: TECH2026',
 'DONE',
 'LOW',
 '2026-06-01 23:59:00',
 5),

('Study Docker',
 'Review docker-compose, volumes and networks',
 'PENDING',
 'MEDIUM',
 '2026-05-20 23:59:00',
 NULL),

('Implement TaskRepository',
 'Create interface and MySqlTaskRepository with JDBC',
 'IN PROGRESS',
 'HIGH',
 '2026-05-15 23:59:00',
 NULL),

('Write unit tests',
 'Cover CreateTaskUseCase and DeleteTaskUseCase with JUnit 5',
 'PENDING',
 'LOW',
 '2026-05-25 23:59:00',
 NULL);

-- ── Notes ─────────────────────────────────────────────────────────────
INSERT INTO note (title, body, task_id) VALUES
('Shops to look for the gift',
 'Department store, bookshop, art supply store on High Street',
 1),

('Maximum budget',
 'Do not spend more than 50 euros',
 1),

('Restaurant phone number',
 '+34 91 123 45 67 — ask for Ana for the special reservation',
 2),

('Special menu',
 'Ana is vegetarian, confirm meat-free options with the restaurant',
 2),

('Last year''s results',
 'High cholesterol: 210. Check if it has improved with the new diet',
 3),

('Key points for the presentation',
 '1. CLI demo\n2. Database schema\n3. Design patterns used\n4. Next steps',
 4),

('PRs to review',
 'chore/docker-setup — chore/project-structure — check init.sql and DatabaseConnection',
 5),

('Landlord bank account',
 'ES12 3456 7890 1234 5678 9012 — Santander Bank',
 6),

('Docker learning resources',
 'Official docs: docs.docker.com — YouTube course: TechWorld with Nana',
 8),

('Classes to implement',
 'TaskRepository.java — MySqlTaskRepository.java — remember to use JDBC correctly',
 9);