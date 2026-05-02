# Task Manager (Java)

---

## Description

A Java task management application with a graphical user interface

The application allows users to:
- Create, edit, and delete tasks
- Assign a priority (LOW, MEDIUM, HIGH) and a status (TODO, IN_PROGRESS, DONE)
- Receive notifications on task changes via a notification engine
- Manage users with basic authentication
- Persist data in JSON format (`tasks.json`)

---

## Project Structure

```
task-manager/      
├── src/
│   ├── Main.java               # Application entry point
│   ├── Task.java               # Task interface
│   ├── TaskImpl.java           # Task implementation
│   ├── TaskList.java           # TaskList interface
│   ├── TaskListImpl.java       # TaskList implementation (with JSON)
│   ├── TaskManagerGUI.java     # Graphical interface (Swing)
│   ├── AuthManager.java        # Authentication management
│   ├── User.java               # User class
│   ├── NotificationEngine.java # Notification engine
│   ├── Status.java             # Status enum (TODO, IN_PROGRESS, DONE)
│   ├── Priority.java           # Priority enum (LOW, MEDIUM, HIGH)
│   ├── tasks.json              # Persisted data
│   ├── _D1_useCase.puml        # Use case diagram (PlantUML)
│   ├── _D2_diagramClass.puml   # Class diagram (PlantUML)
│   ├── _D3_diagramSequence.puml# Sequence diagram (PlantUML)
│   └── _D4_state_transition.puml # State-transition diagram (PlantUML)
└── en/
    └── README.md               # This file
```

---

## Architecture

### Key Interfaces

| Interface | Role |
|-----------|------|
| `Task` | Task contract: edit(), delete(), setStatus() |
| `TaskList` | Task list contract: add, remove, filter |

### Key Classes

| Class | Role |
|-------|------|
| `TaskImpl` | Concrete task implementation |
| `TaskListImpl` | Task list with JSON persistence |
| `TaskManagerGUI` | Main Swing GUI |
| `AuthManager` | Authentication and session management |
| `NotificationEngine` | Sends notifications on task changes |

---

## UML Diagrams

Diagrams are in **PlantUML** format (`.puml`):
- `_D1_useCase.puml` — Use case diagram (actors and interactions)
- `_D2_diagramClass.puml` — Full class diagram
- `_D3_diagramSequence.puml` — Sequence for a task modification
- `_D4_state_transition.puml` — Task state transitions

To view them: [PlantUML Online](https://www.plantuml.com/plantuml/uml/) or the VS Code PlantUML extension.

---

## Prerequisites & Running

- **Java 11+**
- Compile and run:

```bash
cd task_manager
javac *.java
java Main
```

---

## Features

- Task creation with name, description, due date, status, and priority
- Task editing and deletion
- Filtering by status or priority
- Automatic JSON persistence
- Intuitive graphical interface (Java Swing)
- User authentication system
- Notifications on task changes
