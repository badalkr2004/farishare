# FairShareBU

A web application for splitting bills among Bennett University students at campus food outlets.

## Description

FairShareBU is a Java web application that helps students manage and split expenses among friends for meals and other expenses at campus food outlets. The application allows users to create groups, add expenses, track who owes what, and settle payments efficiently.

## Features

- User registration and authentication with JWT tokens
- Group creation and management
- Expense tracking and management
- Expense splitting with custom shares
- Notifications for new expenses and payments
- Chat functionality within groups
- User-friendly interface designed for mobile and desktop

## Technologies Used

- **Backend**: Java Servlets, JSP
- **Frontend**: HTML, CSS, JavaScript
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Server**: Apache Tomcat 7
- **Other Libraries**: 
  - JSTL for JSP templating
  - BCrypt for password hashing
  - Apache Commons DBCP for connection pooling
  - JSON library for API responses

## Prerequisites

To run this application, you need:

- JDK 11 or later
- MySQL Server 8.0 or later
- Maven (or use the included Maven wrapper)
- Web browser

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/badalkr2004/farishare.git
cd fairsharebu
```

### 2. Database Setup

1. Install MySQL if you haven't already
2. Create a database named `fairsharebu`:
   ```sql
   CREATE DATABASE fairsharebu;
   ```
3. Run the initialization script to create tables:
   ```bash
   mysql -u root -p fairsharebu < src/main/resources/sql/init.sql
   ```

### 3. Configuration

The database connection settings are located in `src/main/webapp/WEB-INF/web.xml`. Modify the following parameters if needed:

```xml
<context-param>
    <param-name>jdbcURL</param-name>
    <param-value>jdbc:mysql://localhost:3306/fairsharebu?useSSL=false&amp;allowPublicKeyRetrieval=true</param-value>
</context-param>
<context-param>
    <param-name>jdbcUsername</param-name>
    <param-value>root</param-value>
</context-param>
<context-param>
    <param-name>jdbcPassword</param-name>
    <param-value>password</param-value>
</context-param>
```

Replace `root` and `password` with your MySQL username and password.

## Building and Running

### Using Maven Wrapper

The project includes a Maven wrapper, so you don't need to install Maven separately.

1. Build the project:
   ```bash
   ./mvnw clean package
   ```
   (On Windows, use `mvnw.cmd` instead)

2. Run the application using the embedded Tomcat server:
   ```bash
   ./mvnw tomcat7:run
   ```

3. Access the application in your web browser:
   ```
   http://localhost:9090/fairsharebu/
   ```

### Using Standalone Tomcat

Alternatively, you can deploy the WAR file to a standalone Tomcat server:

1. Build the WAR file:
   ```bash
   ./mvnw clean package
   ```

2. Copy the generated WAR file from `target/fairsharebu-1.0-SNAPSHOT.war` to your Tomcat's `webapps` directory.

3. Start Tomcat and access the application at:
   ```
   http://localhost:8080/fairsharebu-1.0-SNAPSHOT/
   ```

## Development

### Project Structure

- `src/main/java/com/fairsharebu/`: Java source code
  - `controller/`: Servlet controllers
  - `model/`: Data models
  - `dao/`: Data Access Objects
  - `util/`: Utility classes (JWT, Database, etc.)
  
- `src/main/webapp/`: Web resources
  - `WEB-INF/`: Configuration and protected resources
  - `css/`: Stylesheets
  - `js/`: JavaScript files
  - `*.jsp`: JSP pages

### Troubleshooting

- **Port Conflict**: If port 9090 is already in use, modify the port in `pom.xml`:
  ```xml
  <plugin>
      <groupId>org.apache.tomcat.maven</groupId>
      <artifactId>tomcat7-maven-plugin</artifactId>
      <version>2.2</version>
      <configuration>
          <port>8888</port> <!-- Change to any available port -->
          <path>/fairsharebu</path>
      </configuration>
  </plugin>
  ```

- **Database Connection Error**: Ensure MySQL is running and the credentials in `web.xml` are correct.

- **Java Version Issues**: Make sure you're using JDK 11 or later. The application has been tested with Java 11 through 24.




