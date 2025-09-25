# Java Kanban Task Manager

It's a simple and lightweight application developed as part of the Yandex Practicum Java Developer education program.
The project demonstrates core Java principles, object-oriented design, and the implementation of custom in-memory and file-based storage — all built using raw Java code, without any external frameworks.

---

## Technologies Used

- **Java 21**
- **Custom-built REST API**
- **Serialization**
- **JUnit** for unit testing

---

## Features

- CRUD operations:
  - **Tasks**
  - **Epics**
  - **Subtasks** (linked to Epics)
- Track task statuses: `NEW`, `IN_PROGRESS`, `DONE`
- Automatic status calculation for Epics based on subtasks
- Access and manage task history (recently viewed tasks)
- Prioritized task list by start time
- State persistence using file-based storage

---

## Project Structure

- `api/` — Contains classes responsible for handling HTTP requests and responses.
- `comparators/` — Classes that implement the Comparator interface to define custom sorting logic for various objects within the application.
- `controllers/` — Manages the application's business logic and acts as an intermediary between the view and the model, processing user inputs and updating the model accordingly.
- `exceptions/` — Defines custom exceptions and handles error scenarios, ensuring the application can manage and respond to unexpected situations.
- `interfaces/` — Contains interfaces that define the contract for various services or components.
- `models/` — Represents the data structures within the application.
- `resources/` — Stores non-code files such as files for save & download.
- `utils/` — Includes utility classes that provide common functions or helper methods.
- `validators/` — Classes responsible for validating input data, ensuring that it meets the required criteria before being processed by the application.
