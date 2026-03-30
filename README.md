# Smart Study Planner Pro

> A desktop application that thinks about your workload so you don't have to.


<img width="972" height="615" alt="image" src="https://github.com/user-attachments/assets/918577a2-07eb-4ab3-a42a-4a4c3decb4d4" />


---

## What it is

Smart Study Planner Pro is a Java desktop application built for students who have too much to do and not enough time to figure out where to start. Instead of a basic to-do list, it uses a scoring algorithm that looks at each task's deadline, priority, and estimated effort — and automatically ranks your workload so the most urgent, highest-stakes work always surfaces to the top without you having to think about it.

No cloud. No accounts. No subscriptions. Everything runs locally on your machine.

---

## Screenshots

### Main Window
<img width="972" height="615" alt="image" src="https://github.com/user-attachments/assets/a3b6bccb-4d8b-4b26-96dc-1217a7a957f4" />

*The main task view — color-coded by urgency, sorted automatically by score.*

---

### Adding a Task
<img width="972" height="615" alt="image" src="https://github.com/user-attachments/assets/221716d8-aa31-4fa7-81c8-0433d20de5e7" />



*Clean input form — give it a name, deadline, priority, and estimated hours. Done.*

---

### Stats Bar
<img width="972" height="615" alt="image" src="https://github.com/user-attachments/assets/4b4a7074-04ca-4728-a9ec-536a5d41470c" />

*Live overview of pending tasks, completed, overdue, hours left, and completion rate.*

---

### Daily Schedule Generator
<img width="972" height="615" alt="image" src="https://github.com/user-attachments/assets/3813e146-816c-4a59-9757-6c985c31d406" />

*Tell it how many hours you have today — it builds your focused work plan automatically.*

---

### Color-Coded Urgency
<img width="972" height="614" alt="image" src="https://github.com/user-attachments/assets/8d7f0255-4fca-4f9b-a371-162b865f8abc" />

*Overdue in rose. Due today in amber. High priority in yellow. Done in grey.*

---

## Features

- **Intelligent task ranking** — tasks auto-sort using a weighted urgency score based on deadline, priority, and effort
- **Color-coded rows** — instant visual read of what needs attention
- **Stats dashboard** — live counts of pending, completed, overdue tasks and overall completion rate
- **Daily schedule generator** — enter your available hours, get a focused work plan
- **Real-time search** — filter tasks by keyword instantly
- **Filter view** — switch between all tasks, to-do only, or completed
- **Undo / Redo** — full stack-based history across every action
- **CSV export** — export your full task list at any point
- **Deadline warnings** — automatic alerts when a task is due within 24 hours
- **Persistent storage** — tasks save to a local JSON file and reload on next launch

---

## How the scoring works

Every pending task gets a score computed from three factors:

```
urgencyScore = (priority × 3.0)
             + (1 / daysLeft × 10.0)
             + (timeEfficiencyFactor × 1.5)
```

- **Priority weight** — higher priority tasks get a base score boost
- **Urgency weight** — the closer the deadline, the higher this climbs. Overdue tasks get a large bonus so they always sit at the top
- **Time factor** — shorter tasks get a slight nudge, making it easier to pick up quick wins

The list re-sorts itself automatically every time you add, complete, or modify a task.

---

## Tech stack

| Layer | Details |
|---|---|
| Language | Java 17 |
| UI | Java Swing |
| Persistence | JSON file (hand-rolled serializer, zero dependencies) |
| Build | Plain `javac` or Maven |
| Architecture | Clean layered — model / service / repository / ui / utils |

No Spring. No database. No external libraries.

---

## Project structure

```
SmartStudyPlannerPro/
├── src/main/java/com/studyplanner/
│   ├── App.java
│   ├── model/
│   │   ├── Task.java
│   │   └── DailySchedule.java
│   ├── repository/
│   │   ├── TaskRepository.java          ← interface
│   │   └── JsonTaskRepository.java      ← file-backed implementation
│   ├── service/
│   │   ├── TaskService.java             ← core orchestrator
│   │   ├── ScoringEngine.java           ← urgency algorithm
│   │   ├── UndoRedoManager.java         ← stack-based history
│   │   ├── ScheduleGenerator.java       ← daily planner
│   │   └── StatisticsService.java
│   ├── ui/
│   │   ├── MainWindow.java
│   │   ├── TaskFormDialog.java
│   │   ├── TaskTableModel.java
│   │   ├── UrgencyRowRenderer.java
│   │   ├── StatisticsPanel.java
│   │   └── ScheduleDialog.java
│   └── utils/
│       ├── JsonSerializer.java
│       ├── DateUtils.java
│       └── CsvExporter.java
├── pom.xml
└── build.sh
```

---

## Getting started

### Requirements

- Java 17 or higher — download from [adoptium.net](https://adoptium.net) if you don't have it

### Run the pre-built JAR

```bash
java -jar smart-study-planner-pro.jar
```

### Build from source

```bash
# Mac / Linux
chmod +x build.sh
./build.sh
java -jar smart-study-planner-pro.jar
```

```bash
# Or with Maven
mvn package
java -jar target/smart-study-planner-pro.jar
```

```bash
# Or manually with javac
find src/main/java -name "*.java" > sources.txt
javac -encoding UTF-8 -d out/classes @sources.txt
java -cp out/classes com.studyplanner.App
```

### Where data is stored

Tasks are saved automatically to:

```
~/.studyplanner/tasks.json
```

This file is created on first launch. Delete it to start fresh.

---

## Usage

| Action | How |
|---|---|
| Add a task | Click **+ New Task** |
| Complete a task | Select a row → **Mark Done**, or double-click the row |
| Delete a task | Select a row → **Delete** |
| Search | Type in the search box — filters in real time |
| Filter view | Use the dropdown — All tasks / To do / Done |
| Plan your day | **Tools → Plan My Day** |
| Export to CSV | **File → Export to CSV** |
| Undo / Redo | **Edit menu** or `Cmd+Z` / `Cmd+Shift+Z` |

---

## Architecture notes

The codebase follows strict separation of concerns. The UI never contains business logic — `MainWindow` only calls `TaskService` methods and updates the display. All scoring, filtering, and state management lives in the service layer. Persistence is fully isolated behind a `TaskRepository` interface, so the storage format can be changed without touching anything else.

Undo/redo works by taking snapshots of the task list before each mutation. Each snapshot copies the mutable fields of every task rather than holding references to live objects, so later mutations don't corrupt the history.

The scoring engine uses a `Comparator`-based sort so the list can be re-ranked in a single pass after any change.

---


---

## Author

Built by [Devarya Sen]([[https://github.com/yourusername](https://github.com/DevaryaSen)])
