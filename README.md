# Address Book REST API

This Spring Boot application provides a range of endpoints for the maintenance of address books for multiple users

* each user can have multiple address books
* each address book can contain 0 or more contacts
* each contact has a name and 0 or more phone numbers

# Endpoints

* All available endpoints are documented in the included Postman collection
  file `src/test/resources/Address Book.postman_collection.json`
* The same endpoints are contained in the OpenAPI file `src/test/resources/Address Book.open-api.json`
* All endpoints are prefixed with `/api/`

## Building a docker image

* ```./gradlew bootJar```
* ```docker build --build-arg JAR_FILE=build/libs/\*.jar -t addressbook .```

## Running the application in a detached docker container

* ```docker run -p8081:8080 -d --name addressbook addressbook:latest```