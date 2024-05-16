# API
The API is located inside the api-bry/ folder. It is made with Java and Spring Boot, using a PostgreSQL database. It can be executed either in Docker or locally, depending on preference. Note that the two ways use two different databases, hence the data inserted in a Docker-run instance will be accessed by a locally-run instance.

## Requirements
- Maven
- PostgreSQL installed and running on port 5432
  - If PostgreSQL is running in another port, it will be necessary to change the port in the application.properties file
- Docker and Docker-compose (if running with Docker)

## Execution

- Access the api-bry/ folder
- Go to the src/main/resources folder
- Open the application.properties file
  - The default configuration for PostgreSQL on installation is:
    - port: 5432
    - database: 'postgres'
    - username: 'postgres'
    - password: 'postgres'
  - If any of these configurations are different in your local machine, they must be changed in the application.properties file accordingly, otherwise the next step will fail
- Run the command `mvn clean install package`
  - `clean` will dispose of any leftovers or obsolete build files in the application
  - `install` will ensure that all the dependencies are correctly installed
  - `package` will generate a .jar file which will be used by Docker to run the application
- Deploy the application
  - [Docker] Run `docker compose up --build`
  - [Local] Run `mvn spring-boot:run`
- The API will be running on `http://localhost:8080`
- If running with Docker, the database will be running on port 5433