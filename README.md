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


Steps to create, update and delete terraform configuration:<br><br>
1)install terraform on your system<br>
2)Configure aws profiles on aws cli with the following command [aws configure --profile profilename]<br>
3) Create .tfvars file to provide values for the variables used in variables.tf<br>
4) Configure the profile aws_profile var for with the profilename used above<br>
5) cd into the infrastructure repo directory and run following commands<br>
6) terraform init<br>
7) terraform fmt<br>
8) terraform plan -var-file="filename.tfvars"<br>
9) terraform apply -var-file="filename.tfvars"<br>
10) To destroy infra, terraform destroy -var-file="filename.tfvars"<br>

Steps to manage CI-CD:<br>

1)Update CI-CD workflow in .github/workflows folder<br>
2)pull_request.yml file is used for configure the workflow to be run on pull request<br>
3)push.yml is used to configure the workflow to be run on push request<br>
4)AMI is built as a part of the workflow<br>
