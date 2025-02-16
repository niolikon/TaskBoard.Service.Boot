# TaskBoard.Service.Boot
[![Build](https://github.com/niolikon/TaskBoard.Service.Boot/actions/workflows/maven.yml/badge.svg)](https://github.com/niolikon/TaskBoard.Service.Boot/actions)
[![Package](https://github.com/niolikon/TaskBoard.Service.Boot/actions/workflows/publish-maven.yml/badge.svg)](https://github.com/niolikon/TaskBoard.Service.Boot/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

Task Board Service (Spring Case Study)

# Overview

📚 **TaskBoard.Service.Boot** is a simple Web API project designed to manage Personal Tasks as a Todo list.
This project demonstrates fundamental software development concepts such as BDD (Behavior-Driven Development), unit testing, and clean architecture principles.

---

## 🚀 Features

- **Todo Management**: Manage To-dos with CRUD operations.
- **Dependency Injection**: Decouple components for better testability and maintainability.
- **Rest exceptions Management**: Centralize JSON error response management with RestControllerAdvice for better separation of concerns.

---

## 📖 User Stories

- 🆕 [Todo Marking](https://github.com/niolikon/TaskBoard.Service.Boot/issues/1)

---

## 🛠️ Getting Started

### Prerequisites

- **Java 17+**
- **Maven 3+**
- **Docker** (optional, to deploy the service on container)


### Quickstart Guide

1. Clone the repository:
   ```bash
   git clone https://github.com/niolikon/TaskBoard.Service.Boot.git
   cd TaskBoard.Service.Boot
   ```

2. Compile the project:
   ```bash
   mvn clean install
   ```
   
3. Execute the project:
   ```bash
   mvn spring-boot:run
   ```

### Deploy on container

1. Configure credentials on a .env file as follows
   ```
    DB_NAME=todolist
    DB_USER=appuser
    DB_PASSWORD=apppassword
    KEYCLOAK_DB_PASSWORD=supersecretkeycloak
    KEYCLOAK_ADMIN_PASSWORD=adminpassword
   ```

2. Compile the project:
   ```bash
   mvn clean package
   ```
   
3. Create project image
   ```bash
   docker build -t taskboard-service-boot:latest .
   ```

4. Compose docker container
   ```bash
   docker-compose up -d
   ```

---

## 📬 Feedback

If you have suggestions or improvements, feel free to open an issue or create a pull request. Contributions are welcome!

---

## 📝 License

This project is licensed under the MIT License.

---
🚀 **Developed by Simone Andrea Muscas | Niolikon**

