# TMApp
Task Management Application - Developed for PrediSurge technical assessment

## Description
This project implements a simple Task Management Application composed of:
- a backend REST API in Java (Spring)
- a frontend Vue.js interface

## Requirements
To build and run this project, you'll need at least:
- Java 17
- node 20.11
- npm 10.4

## Build & Run
The backend works with Java17:
```bash
cd demo
java -cp demo com.example.demo.DemoApplication
```

To run the frontend interface server, you need npm:
```bash
cd frontend
npm run serve
```

You'll also need a working PostreSQL database running.
Fill the file `demo/src/main/resources/application.properties` with the credentials for database connection.

Then, the project is accessible on http://localhost:5173.