This repository contains the API and Web Client for the test for a Software Developer position at BRy.

# API
The API is located inside the api-bry/ folder. It is made with Java and Spring Boot, using a PostgreSQL database. It can be executed either in Docker or locally, depending on preference. Note that the two ways use two different databases, hence the data inserted in a Docker-run instance will be accessed by a locally-run instance.

## Requirements
- Maven
- Docker and Docker-compose (if running with Docker)
- PostgreSQL installed and running (if running locally)

## Execution

- Open a terminal inside the api-bry/ folder
- Run the command `mvn clean install package`
  - `clean` will dispose of any leftovers or obsolete build files in the application
  - `install` will ensure that all the dependencies are correctly installed
  - `package` will generate a .jar file which will be used by Docker to run the application
- Deploy the application
  - [Docker] Run `docker compose up --build`
  - [Local] Run `mvn spring-boot:run`