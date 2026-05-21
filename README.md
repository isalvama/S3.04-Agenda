# 📋 CLI Agenda

A command-line agenda application built with Java 21, MySQL, and Docker — designed to manage tasks, notes, and events from your terminal.

This is a team project for **Sprint 3.04** at IT Academy Barcelona Activa, focused on applying **design patterns** (Strategy, Singleton, Command) in a real, working application.

## 👥 Team

| Developer | Domain | Branch |
|-----------|--------|--------|
| **Jess Borges** | Task management + Strategy pattern + App wiring + tests | `feature/task-domain` |
| **Adri Elías** | Note management + Value Objects + Docker/pom.xml config | `feature/notes` |
| **Inés Salvamá** | Event management + EventController + ConsoleReader + Database schema | `refactor/event-domain` |

## What it does

The app lets you manage three interconnected domains through a simple CLI menu:

- **Tasks** — Create, list, update, delete, mark as done. Filter by status (pending, completed, all) using the Strategy pattern. Each task can optionally link to an event and have a priority level.
- **Notes** — Attach notes to tasks. Useful for adding context, reminders, or details to any task without cluttering the task itself.
- **Events** — Schedule events (birthdays, appointments, reminders, or other) with optional recurrence (yearly, monthly, weekly, daily, hourly). At startup, the app automatically notifies upcoming and past events. Tasks can reference events via foreign key.

## Architecture

We followed a **layered architecture** with clear separation of concerns:

```
Controller → Service → Repository → Model
   (CLI)    (logic)     (JDBC)    (entity)
```

Each domain (task, note, event) has its own package with the same structure: `controller/`, `dto/`, `model/`, `repository/`, `service/`, and `exception/`. Shared utilities live in the `shared/` package.

### Design patterns used

**Strategy** — Used across all three domains:
- *Task listing*: interchangeable strategies (`ListAllStrategy`, `ListPendingStrategy`, `ListCompletedStrategy`) implement `TaskStrategy`. The service calls `execute()` without knowing which strategy is active.
- *Note listing*: `NoteServiceImpl` selects a `NoteStrategy` at runtime — `GetAllNotesStrategy` (all notes) or `GetAllByTaskIdStrategy` (filtered by task). Both implement `NoteStrategy` and expose a single `execute()` method that returns a list of `NoteResponse`.
- *Event notification*: `EventNotifier` uses `ListEventsStrategy` implementations (`TodayEventsStrategy`, `LastTwoDaysEventsStrategy`, `NextSevenDaysEventsStrategy`) to filter and display events by time window at startup.

**Singleton** — `DatabaseConnection` ensures a single shared connection across all repositories, with `synchronized` access and retry logic (5 attempts, 3-second delay) to handle Docker Compose startup timing.

**Command** — Both `TaskController` and `EventController` map menu options to method references in a `Map<String, Runnable>` instead of a switch block, making the menu extensible without modifying the routing logic.

### DTOs

We use Java **records** (`TaskRequest`, `TaskResponse`, `CreateEventRequest`, `EventResponse`, `UpdateEventRequest`, `NoteRequest`, `NoteResponse`, etc.) to separate what goes in from what comes out. The controller never touches the entity directly — it only sees immutable DTOs. This protects the domain model and prevents accidental mutations.

## Tech stack

- **Java 21** (Eclipse Temurin)
- **Maven** for dependency management and build
- **MySQL 8.0** via Docker Compose
- **JDBC** with PreparedStatement (no ORM)
- **JUnit 5** + **Mockito** for testing
- **Docker Compose** for database + Adminer + optional app container
- **Adminer** for visual database inspection

## Getting started

### Prerequisites

- Java 21 (Temurin recommended)
- Docker & Docker Compose
- Maven (or use your IDE's built-in Maven)

### 1. Clone the repository

```bash
git clone https://github.com/isalvama/S3.04-Agenda.git
cd S3.04-Agenda
```

### 2. Configure environment variables

```bash
cp .env.example .env
```

Edit `.env` with your values:

```env
MYSQL_ROOT_PASSWORD=MyR00tP@ss!
MYSQL_DATABASE=agenda_db
MYSQL_USER=agenda_user
MYSQL_PASSWORD=Ag3nd@Pass#2026

DB_HOST=localhost
DB_PORT=3306
DB_NAME=agenda_db
DB_USER=agenda_user
DB_PASS=Ag3nd@Pass#2026

MYSQL_PORT=3306
ADMINER_PORT=8080
```

> **Note:** When running the Java app locally (not in Docker), set `DB_HOST=localhost`. When running inside the Docker container, use `DB_HOST=mysql-db`.

### 3. Start the database

```bash
docker compose up -d
```

This starts MySQL and Adminer. The `init.sql` script creates the schema automatically, and `seed.sql` populates it with sample data (tasks, events, and notes).

You can inspect the database at `http://localhost:8080` (Adminer).

### 4. Run the application

From your IDE, run `Main.java`. Or from terminal:

```bash
mvn clean package -DskipTests
java -jar target/cli-agenda-1.0.jar
```

Or run everything in Docker (including the Java app):

```bash
docker compose --profile interactive up
```

### 5. Run tests

```bash
mvn test
```

Tests cover all three domains — service logic, repository behaviour, DTOs, value objects, console reader, app menu routing, event formatting, event notification, and recurring event processing. Tests use Mockito to mock the repository layer, so no database connection is needed.

## Project structure

```
src/
├── main/java/org/agenda/
│   ├── app/
│   │   ├── App.java              # Wires all domains, starts scheduler + notifier
│   │   └── Main.java
│   ├── event/
│   │   ├── controller/
│   │   │   ├── EventController.java    # CLI menu (Command pattern)
│   │   │   ├── EventFormatter.java     # Formats EventResponse for display
│   │   │   └── EventNotifier.java      # Startup event summary (Strategy pattern)
│   │   ├── dto/
│   │   │   ├── CreateEventRequest.java
│   │   │   ├── EventResponse.java
│   │   │   └── UpdateEventRequest.java
│   │   ├── model/
│   │   │   ├── CalendarEvent.java      # Entity with recurrence logic
│   │   │   ├── EventSchedule.java      # Enum: YEARLY, MONTHLY, WEEKLY, DAILY, HOURLY
│   │   │   └── EventType.java          # Enum: BIRTHDATE, APPOINTMENT, REMINDER, OTHER
│   │   ├── repository/
│   │   │   ├── EventRepository.java
│   │   │   ├── EventRepositoryImpl.java
│   │   │   ├── EventDataAccessConnection.java
│   │   │   ├── EventNotFoundException.java
│   │   │   ├── EventNotSavedException.java
│   │   │   └── EventNotUpdatedException.java
│   │   └── service/
│   │       ├── strategy/
│   │       │   ├── ListEventsStrategy.java         # Interface
│   │       │   ├── TodayEventsStrategy.java
│   │       │   ├── LastTwoDaysEventsStrategy.java
│   │       │   └── NextSevenDaysEventsStrategy.java
│   │       ├── EventService.java
│   │       ├── EventServiceImpl.java
│   │       ├── EventRecurringService.java  # Auto-schedules next occurrence of past recurring events
│   │       └── EventResponseMapper.java
│   ├── note/
│   │   ├── controller/         # NoteController
│   │   ├── dto/                # NoteRequest, NoteResponse
│   │   ├── exception/          # NoteNotFoundException
│   │   ├── model/              # Note
│   │   ├── repository/         # NoteRepository, NoteRepositoryImpl
│   │   └── service/
│   │       ├── strategy/       # NoteStrategy, GetAllNotesStrategy, GetAllByTaskIdStrategy
│   │       ├── NoteService.java
│   │       └── NoteServiceImpl.java
│   ├── shared/
│   │   ├── config/             # DatabaseConnection (Singleton)
│   │   ├── console/            # ConsoleReader, InvalidInputTypeException
│   │   ├── domain/
│   │   │   ├── exception/      # DomainException, InvalidTitleException,
│   │   │   │                   # InvalidDescriptionException
│   │   │   └── value_object/   # Title, Description
│   │   └── exception/          # AgendaException, DataAccessException
│   └── task/
│       ├── controller/         # TaskController (Command pattern)
│       ├── dto/                # TaskRequest, TaskResponse (records)
│       ├── exception/          # TaskNotFoundException, TaskValidationException
│       ├── model/              # Task, Status, Priority
│       ├── repository/         # TaskRepository, TaskRepositoryImpl
│       └── service/
│           ├── strategy/       # TaskStrategy, ListAllStrategy, ListPendingStrategy,
│           │                   # ListCompletedStrategy
│           ├── TaskService.java
│           └── TaskServiceImpl.java
├── test/java/org/agenda/
│   ├── event/
│   │   ├── controller/         # EventControllerTest, EventFormatterTest, EventNotifierTest
│   │   ├── model/              # CalendarEventTest
│   │   ├── repository/         # MySQLEventRepositoryTest
│   │   └── service/            # EventServiceImplTest, EventRecurringServiceTest,
│   │                           # EventResponseMapperTest, strategy/
│   ├── note/
│   │   ├── controller/         # NoteControllerTest
│   │   ├── repository/         # NoteRepositoryTest
│   │   └── service/            # NoteServiceTest
│   ├── shared/
│   │   ├── console/            # ConsoleReaderTest
│   │   └── domain/value_object/ # TitleTest, DescriptionTest
│   ├── task/service/           # TaskServiceTest
│   └── AppTest.java
sql/
├── init.sql
└── seed.sql
.env.example
.gitignore
docker-compose.yml
Dockerfile
pom.xml
README.md
```

## Task domain in depth

### What it does

The Task domain is the central piece of the application and the most feature-rich domain. It allows you to:

- **Create** a task — title (required), body (optional), priority (HIGH / MEDIUM / LOW, optional), expiration date (defaults to +7 days), and an optional link to an event by ID.
- **List all tasks** — shows every task regardless of status.
- **List pending tasks** — filtered view, only `PENDING` tasks.
- **List completed tasks** — filtered view, only `DONE` tasks.
- **Mark as done** — shortcut to set a task's status to `DONE` without a full update.
- **Update a task** — edit any field; empty input keeps the current value.
- **Delete a task** — removes the task and cascades deletion to its notes.
- **Find by ID** — retrieve a single task.

### Strategy pattern (`TaskStrategy`)

Task listing uses the Strategy pattern so the controller can swap the retrieval logic without touching the service:

| Strategy | Behaviour |
|----------|-----------|
| `ListAllStrategy` | `repository.findAll()` — returns every task |
| `ListPendingStrategy` | `repository.findAllByStatus(PENDING)` — only pending |
| `ListCompletedStrategy` | `repository.findAllByStatus(DONE)` — only completed |

`TaskController` holds all three strategy instances and passes the right one to `taskService.listTasks(strategy)`. The service just calls `strategy.execute()` — it has no idea which strategy it received.

### Command pattern (`TaskController`)

Menu options are stored in a `Map<String, Runnable>` initialised in `initializeMenu()`. Routing is a single `menuActions.getOrDefault(choice, fallback).run()` — no switch, no if-else chain.

### Key design decisions

- **Value Objects** — `Title` and `Description` wrap task title and body, validating them at construction time (shared with Note and Event).
- **Status** — `PENDING`, `IN_PROGRESS`, `DONE`. The `fromSqlValue()` helper on the enum parses the string coming from the DB.
- **Priority** — Optional enum (`HIGH`, `MEDIUM`, `LOW`). Tasks without priority display `N/A` in the CLI.
- **Event link** — A task can optionally reference an event via `event_id` (FK). If the event is deleted, the FK is set to `NULL` automatically (`ON DELETE SET NULL`).
- **`markAsDone`** — dedicated service method that only flips the status, keeping the update flow clean.

### Exceptions

| Exception | When thrown |
|-----------|-------------|
| `TaskNotFoundException` | `getById`, `update`, `markAsDone`, `delete` — task ID does not exist |

## Note domain in depth

### What it does

The Note domain is fully implemented and wired into the main application menu. Notes always belong to a task — you cannot create a note without a valid task ID. It allows you to:

- **Create** a note — title (required), body (optional), linked to an existing task.
- **List all notes** — shows every note in the database.
- **View note by ID** — retrieve a single note.
- **List notes by task ID** — filter all notes belonging to a specific task.
- **Update** a note — change title, body, or re-link to a different task (task must exist).
- **Delete** a note — removes the note by ID.

### Strategy pattern (`NoteStrategy`)

Note listing uses the Strategy pattern to select the retrieval logic at runtime:

| Strategy | Behaviour |
|----------|-----------|
| `GetAllNotesStrategy` | Calls `repository.findAll()` — returns every note |
| `GetAllByTaskIdStrategy` | Calls `repository.findAllByTaskId(taskId)` — filters by task |

`NoteServiceImpl` instantiates the appropriate strategy and calls `execute()`, keeping the service decoupled from the query logic. Adding a new filter (e.g., by date or keyword) means creating one new class without touching existing code.

### Key design decisions

- **Value Objects** — `Title` and `Description` (shared with tasks and events) wrap note title and body, enforcing validation rules at construction time.
- **Task existence check** — `create` and `update` verify the target task exists via `TaskRepositoryImpl.existsById()` before persisting, returning a clear error if not.
- **Cascade delete** — Notes are deleted automatically by the database (`ON DELETE CASCADE`) when their parent task is removed.
- **`NoteController` also uses the Command pattern** — menu options are stored in a `Map<String, Runnable>`, consistent with the other controllers.

### Exceptions

| Exception | When thrown |
|-----------|-------------|
| `NoteNotFoundException` | `getById`, `update`, `delete` — note ID does not exist |

## Event domain in depth

### What it does

The Event domain is fully implemented and wired into the main application menu. It allows you to:

- **Create** an event — title (required), description (optional), date (defaults to tomorrow), type, and an optional recurrence schedule.
- **List all events** — shows every event in the database.
- **List upcoming events** — asks for a number of days and shows events within that window.
- **Update an event** — edit any field of an existing event by ID.
- **Delete an event** — removes the event and returns a summary.
- **Find by ID** — retrieve a single event.

### Recurrence (`EventRecurringService`)

When the app starts (and every minute afterward via a `ScheduledExecutorService`), `EventRecurringService` scans all events. Any past event that has a schedule set automatically gets a **new occurrence** saved to the database with the next calculated date, and its own schedule is cleared. This prevents infinite generation while keeping the next date visible.

Supported schedules: `YEARLY`, `MONTHLY`, `WEEKLY`, `DAILY`, `HOURLY`.

### Startup notification (`EventNotifier`)

On launch, `EventNotifier` prints a categorised summary using three `ListEventsStrategy` implementations:

| Strategy | Window |
|----------|--------|
| `LastTwoDaysEventsStrategy` | Events from the last 2 days |
| `TodayEventsStrategy` | Events happening today |
| `NextSevenDaysEventsStrategy` | Events in the next 7 days |

### Past-date warning

When creating or updating an event with a date in the past, `CalendarEvent.checkIfDateIsInThePast()` returns an `Optional<String>` warning that is included in the `EventResponse` and displayed in the controller — without blocking the operation.

### Exceptions

| Exception | When thrown |
|-----------|-------------|
| `EventNotFoundException` | `getById`, `update`, `delete` — event ID does not exist |
| `EventNotSavedException` | `create` — repository returns empty after save |
| `EventNotUpdatedException` | `updateById` — update affected 0 rows |

## Database schema

Three tables with relationships:

- **event** — Standalone. Has type (BIRTHDATE, APPOINTMENT, REMINDER, OTHER) and optional schedule for recurrence.
- **task** — Can optionally reference an event via `event_id` (FK, ON DELETE SET NULL). Has status (PENDING, IN PROGRESS, DONE) and optional priority (HIGH, MEDIUM, LOW).
- **note** — Always belongs to a task via `task_id` (FK, ON DELETE CASCADE). If the task is deleted, its notes go with it.

Indexes on `status`, `priority`, `event.date`, and `event.type` for query performance.

## Git workflow

We used **feature branches** with pull requests into `dev`:

1. Each developer worked on their own branch (`feature/task-domain`, `feature/notes`, `refactor/event-domain`)
2. PRs were reviewed before merging into `dev`
3. Conventional commits: `feat:`, `fix:`, `test:`, `refactor:`, `docs:`
4. `main` branch stays clean — only receives stable, tested code from `dev`

## What we'd improve

- Extract input parsing from `TaskController` into a dedicated `TaskInputHandler` class
- Replace generic `RuntimeException` in `TaskRepositoryImpl` with custom `DataAccessException`
- Add integration tests that hit a real database (e.g., with Testcontainers)
- Add input length validation and date validation (reject past dates) in the service layer
- Persist the recurrence-clearing logic atomically (currently two separate `updateById` calls)

---

