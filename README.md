# webapp

Repository for CSYE-6225

Dependencies:
Java 11,
mvn v3.8.2,
PostgreSQL v13.0

Steps to Built the Application:
1) Connect to PostgresServer and run the command CREATE DATABASE CSYE6225
2) Clone the application
3) Open application properties and update  spring.datasource.username and spring.datasource.password based on your local PostgresServer setting
4) Open terminal and cd into webapp
5) Run following command "mvn dependency:resolve"
6) Run following command "mvn spring-boot:run"
7) To access the API's open Postman and import [Collection](https://github.com/Olly96/webapp/blob/main/CSYE-6225%20webapp.postman_collection.json)

Steps to Run Tests:<br>
1) mvn test
change
