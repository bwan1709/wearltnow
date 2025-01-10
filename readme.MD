# **Welcome to the Project Repository**

Hi everyone, my name is Quan. Welcome to my project! This guide will help you get started with the source code and understand how to use it effectively.

---

## **Project Overview**
This project is designed to build a back-end service for an eCommerce project.

---

## **Getting Started**
To get started with the project, ensure you have the following prerequisites installed:

- **Java Development Kit (JDK) 8 or higher**  
  Follow this guide: [How to Install JDK on Windows 11](https://www.solveyourtech.com/how-to-install-jdk-on-windows-11-a-step-by-step-guide/)

- **MySQL 8.0 or higher**  
  Installation guide: [Install MySQL on Windows](https://www.w3schools.com/mysql/mysql_install_windows.asp)

- **Redis**  
  Setup instructions: [Install Redis Server on Windows](https://stackjava.com/redis/huong-dan-cai-dat-redis-server-tren-windows.html#google_vignette)

---

## **Installation**

1. Clone the repository:
   ```bash
   git clone https://github.com/bwan1709/wearltnow.git
   ```

2. Navigate to the project directory:
   ```bash
   cd your-repository
   ```

3. Install dependencies using Maven:
   ```bash
   mvn clean install
   ```

---

## **Usage**

### Running the Project

1. Start the development server:
   ```bash
   mvn spring-boot:run
   ```

2. Access the application in your browser at:
   ```
   http://localhost:8000
   ```

### Running SQL Files

- To set up the database schema, run the SQL files located in the `src/database` directory.
- Example command to execute an SQL file:
  ```bash
  mysql -u <username> -p <password> < database_name < src/database/schema.sql
  ```

Replace `<username>`, `<password>`, and `<database_name>` with your MySQL credentials and the desired database name.

---

## **API Documentation**

- To get the Postman collection:
  Open the `api-documentation` file in the repository and import it into your Postman workspace.

---
