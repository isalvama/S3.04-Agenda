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
- **Events** — Schedule events (birthdays, appointments, reminders) with optional recurrence (yearly, monthly, weekly, daily, hourly). Tasks can reference events via foreign key.

## Architecture

We followed a **layered architecture** with clear separation of concerns:

```
Controller → Service → Repository → Model
   (CLI)    (logic)     (JDBC)    (entity)
```

Each domain (task, note, event) has its own package with the same structure: `controller/`, `dto/`, `model/`, `repository/`, `service/`, and `exception/`. Shared utilities live in the `shared/` package.

### Design patterns used

**Strategy** — Task listing uses interchangeable strategies (`ListAllStrategy`, `ListPendingStrategy`, `ListCompletedStrategy`) that implement `TaskStrategy`. The service doesn't know which strategy it's running — it just calls `execute()`. Adding a new filter (e.g., by priority) means creating one new class without touching existing code.

**Singleton** — `DatabaseConnection` ensures a single shared connection across all repositories, with `synchronized` access and retry logic (5 attempts, 3-second delay) to handle Docker Compose startup timing.

**Command** — `TaskController` maps menu options to method references in a `Map<String, Runnable>` instead of a switch block, making the menu extensible without modifying the routing logic.

### DTOs

We use Java **records** (`TaskRequest`, `TaskResponse`, etc.) to separate what goes in from what comes out. The controller never touches the entity directly — it only sees immutable DTOs. This protects the domain model and prevents accidental mutations.

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
MYSQL_ROOT_PASSWORD=rootpass
MYSQL_DATABASE=agenda
MYSQL_USER=agenda_user
MYSQL_PASSWORD=agenda_pass

DB_HOST=localhost
DB_PORT=3306
DB_NAME=agenda
DB_USER=agenda_user
DB_PASS=agenda_pass

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

Currently **106 tests** covering all three domains — service logic, repository behavior, DTOs, value objects, console reader, and app menu routing. Tests use Mockito to mock the repository layer, so no database connection is needed.

## Project structure

```
src/
├── main/java/org/agenda/
│   ├── app/
│   │   ├── App.java
│   │   └── Main.java
│   ├── event/
│   │   ├── controller/         # EventController
│   │   ├── dto/                # CreateEventRequest, EventResponse, UpdateEventRequest
│   │   ├── model/              # CalendarEvent, EventSchedule, EventType
│   │   ├── repository/         # EventRepository, EventRepositoryImpl,
│   │   │                       # EventDataAccessConnection, EventNotFoundException,
│   │   │                       # EventNotSavedException
│   │   └── service/            # EventService, EventServiceImpl
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
│   │   ├── dto/                # UpdateEventRequestTest
│   │   ├── model/              # CalendarEventTest
│   │   ├── repository/         # MySQLEventRepositoryTest
│   │   └── service/            # EventServiceTest
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
- Wire the Event domain into `App.java` main menu
- Add input length validation and date validation (reject past dates) in the service layer

---

