# FairShareBU

A web application for splitting bills and managing expenses among Bennett University students, particularly at campus food outlets.

<p align="center">
  <img src="src/main/webapp/img/logo.png" alt="FairShareBU Logo" width="150">
</p>

## Description

FairShareBU is a Java web application designed to simplify expense sharing among friends. It allows users to create groups, add expenses, track balances, and settle payments efficiently. The application is particularly tailored for Bennett University students who want to manage shared expenses at campus food outlets, apartments, trips, and other group activities.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Building and Running](#building-and-running)
- [Usage Guide](#usage-guide)
- [Development](#development)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Features

### User Management
- **User Registration**: Create an account with email and password
- **User Authentication**: Secure login with JWT token-based authentication
- **Profile Management**: Update personal information and profile picture
- **Account Settings**: Change password and update notification preferences

### Group Management
- **Create Groups**: Set up expense sharing groups with custom names and descriptions
- **Invite Members**: Add friends to your expense groups
- **Group Settings**: Customize group preferences and privacy settings
- **Group Image**: Upload a representative image for each group
- **Group Roles**: Group creator has administrative privileges

### Expense Tracking
- **Add Expenses**: Record expenses with descriptions, amounts, and dates
- **Expense Categories**: Categorize expenses (food, travel, entertainment, etc.)
- **Payment Methods**: Track how expenses were paid (cash, UPI, card, etc.)
- **Receipt Upload**: Attach receipt images to expenses for verification
- **Expense History**: View all past expenses with detailed information

### Bill Splitting
- **Equal Splits**: Divide expenses equally among group members
- **Custom Splits**: Assign different amounts to different members
- **Percentage Splits**: Split by percentage instead of fixed amounts
- **Exclude Members**: Option to exclude certain members from specific expenses

### Payment and Settlement
- **Balance Dashboard**: See at a glance who owes what and to whom
- **Settlement Suggestions**: Get recommendations on how to settle debts efficiently
- **Record Payments**: Track when members settle their debts
- **Payment History**: Maintain a record of all settlements
- **Settlement Reminders**: Notify users of pending payments

### Notifications and Communication
- **Real-time Notifications**: Get alerts for new expenses, payments, and group invites
- **Email Notifications**: Receive important updates via email
- **Group Chat**: Communicate with group members within the application
- **Comment on Expenses**: Discuss specific expenses within the group

### Analytics and Reports
- **Expense Summary**: View expense statistics and trends
- **Monthly Reports**: Track spending patterns over time
- **Personal Finance**: See your spending across different groups and categories
- **Export Data**: Download expense data in CSV format for personal records

## Technologies Used

- **Backend**: Java Servlets, JSP
- **Frontend**: HTML, CSS, JavaScript, Bootstrap 5
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
- Web browser (Chrome, Firefox, Edge recommended)

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/badalkr2004/fairshare.git
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
   mysql -u root -p fairsharebu < src/main/resources/sql/fairshare_schema.sql
   ```
   
   This script creates all required tables for the application. The schema file includes optional sample data that you can uncomment for testing purposes.

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

## Usage Guide

### Getting Started

1. **Registration**:
   - Visit the homepage and click "Sign Up"
   - Fill in your details (name, email, password)
   - Verify your email if required

2. **Login**:
   - Use your credentials to log in
   - The dashboard will display your groups and recent activities

### Creating and Managing Groups

1. **Create a Group**:
   - Click the "New Group" button on the dashboard
   - Enter a group name, description, and optional image
   - Add members by entering their email addresses or usernames
   - Click "Create Group" to finalize

2. **Group Dashboard**:
   - View all group expenses and balances
   - See who owes what to whom
   - Access group settings to modify details or add/remove members

### Recording Expenses

1. **Add a New Expense**:
   - From a group page, click "Add Expense"
   - Enter expense details (amount, description, date)
   - Select how to split the expense (equally, custom amounts, percentages)
   - Upload a receipt image if available
   - Click "Save" to add the expense

2. **View Expense Details**:
   - Click on any expense to see full details
   - View who paid, who owes what, and any comments
   - Add comments or edit the expense if needed

### Settling Debts

1. **View Balances**:
   - The "Balances" tab shows who owes money and who is owed
   - Positive balances indicate money owed to you
   - Negative balances indicate money you owe others

2. **Settle Up**:
   - Click "Settle Up" from the group or dashboard
   - Select the person you're settling with
   - Enter the amount being paid
   - Confirm the payment to update balances
   - Track payment history under "Payments" tab

### Notifications and Communication

1. **Check Notifications**:
   - The bell icon shows your new notifications
   - Get alerts for new expenses, payments, and group invites

2. **Group Chat**:
   - Use the chat feature within groups to discuss expenses
   - Mention specific expenses or members in discussions

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

## Troubleshooting

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

- **Login Problems**: If you're unable to log in, try resetting your password. If that doesn't work, check the server logs for more information.

- **Missing Images**: If images aren't displaying, ensure the upload directories exist and have proper permissions.

## Contributing

Contributions to FairShareBU are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.




