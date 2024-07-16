# Request-Management
Project for server request management

## Instructions for running

Below are the steps to configure the application on your machine:

1. Clone the repository;
2. Install PostgreSQL, configure it, and create a database with name "requestdb" (or another name of your choice);
3. In file [application.yml](https://github.com/icarusrodrigues/Request-Management/blob/main/src/main/resources/application.yml) put your database credentials (user, password and name of database in url field if you create a database with another name);

> [!NOTE]  
> The application uses flyway to manage changes to the database, so when you run the application for the first time, the necessary tables will be automatically created.

And to execute the application run the following commands:

1. `mvn clean install`/`mvn clean install -DskipTests` (These two commands install all the dependencies in [pom.xml](https://github.com/icarusrodrigues/Request-Management/blob/main/pom.xml) file, the first one run all the unit tests after installing the dependencies, and the second one skip these tests.)
2. `mvn spring-boot:run` (This command start the application)

## Instructions for executing the requests:

When running the application for the first time, the necessary tables will be created by the flyway migrations. For testing purposes 3 users will be created in user's table, one of each existing User Type: **ADMIN**, **TEACHER** and **TECHNICIAN**.

The credentials of these users are:

- ADMIN
  - cpf: 000.000.000-00
  - username: adminTest
  - email: admin@email.com
  - password: 1234

- TEACHER
  - cpf: 111.111.111-11
  - username: teacherTest
  - email: teacher@email.com
  - password: 1234

- TECHNICIAN
  - cpf: 222.222.222-22
  - username: technicianTest
  - email: technician@email.com
  - password: 1234

All types of users can view all users registered in the database, update their own information and delete their own registration in the database.

Specifically, each user type can do what is shown below:

- Teacher User:
  - Create a request, defining its characteristics, 
  - View their own requests, 
  - Update and delete a request made by themselves.
- Technician User:
  - View all the requests in the database, or select a specific request, 
  - Approve or disapprove a request. 
- Admin User:
  - Everything a Teacher user and a Technical user can do,
  - Create new users of any type.

It's possible to register your own user in database, accessing the [register endpoint](http://localhost:8080/auth/sign-up), you'll need to pass the following information:

- username (String),
- cpf (String),
- email (String)
- registrationNumber (String),
- String (String),
- password (String)
- birthDate (LocalDate - format: yyyy-MM-dd)
- gender (MALE, FEMALE, NON_SPECIFICATION)
- userType (TEACHER, TECHNICIAN)

> [!NOTE]
> The application uses a class to validate the user's CPF, so you will need a real CPF to create a user. And it is possible to pass a CPF with (`xxx.xxx.xxx-xx`) or without (`xxxxxxxxxxx`) symbols, the code will save the CPF correctly in the database.

> [!NOTE]
> It's not possible to create a user of type ADMIN, to use one of this type, login with the Admin user that is saved in the database, and if you want, create your own Admin user accessing the create user endpoint.

Accessing the [login endpoint](http://localhost:8080/auth/login) you'll need to pass two information:

- "auth" (can be CPF with or without the symbols, email or username)
- "password"

> [!NOTE]
> If you want to login with the users created by flyway, will not be possible to pass the CPF in "auth", as they are not valid, so use email or username. Anyway, if you login with a user registered by you will be possible to pass the CPF in "auth".

After logging in, you will be able to manage Requests. The information required to create instances of this entity is (TEACHER, ADMIN):

- area (String)
- requestType (POSTGRADUATE, MASTERS_DEGREE, DOCTORATE_DEGREE)
- workLoad (Integer)
- totalCost (Float)

And to [approve](http://localhost:8080/requests/approve/1) or [disapprove](http://localhost:8080/requests/disapprove/1) a Request it's necessary to pass the id of the Request. And if you disapprove a request, you must provide a reason for disapproval in the request body as a JSON as follows:

``{"reason": "reason details"}``

## Postman

In the application there is also a [postman collection](https://github.com/icarusrodrigues/Request-Management/tree/main/collection/Request_Management_Collection.postman_collection.json) with all endpoints.

> [!NOTE]
> After doing the login, the user token will be automatically added in all the collection's endpoints. 

## Swagger UI

The application has the Swagger tool to document it. To access it, run the application and access the url: (http://localhost:8080/swagger-ui/index.html)

In Swagger you can access all endpoints and generate requests:

And on each endpoint, you can see the type of response you might receive: