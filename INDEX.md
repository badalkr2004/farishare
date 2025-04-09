# FairShare Project Index

## Project Structure

```
fairshare/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── fairsharebu/
│       │           ├── controller/    # Controllers for handling HTTP requests
│       │           ├── dao/          # Data Access Objects for database operations
│       │           ├── model/        # Data models and entities
│       │           └── util/         # Utility classes and helper functions
│       ├── resources/                # Configuration files and resources
│       └── webapp/                   # Web application files
├── target/                          # Compiled files and build output
├── .mvn/                            # Maven wrapper files
├── .vscode/                         # VS Code configuration
├── pom.xml                          # Maven project configuration
└── readme.md                        # Project documentation
```

## Main Components

### Controllers (`src/main/java/com/fairsharebu/controller/`)
- Handle HTTP requests and responses
- Implement REST endpoints
- Manage application flow

### DAO Layer (`src/main/java/com/fairsharebu/dao/`)
- Data Access Objects
- Database operations and queries
- Data persistence logic

### Models (`src/main/java/com/fairsharebu/model/`)
- Data entities
- Business logic models
- Data transfer objects

### Utilities (`src/main/java/com/fairsharebu/util/`)
- Helper classes
- Common utilities
- Shared functionality

## Build and Dependencies
- Maven-based project
- Java application
- Web application structure

## Configuration
- Maven configuration in `pom.xml`
- VS Code settings in `.vscode/`
- Resource files in `src/main/resources/`

## Documentation
- Project documentation in `readme.md`
- This index file (`INDEX.md`) 