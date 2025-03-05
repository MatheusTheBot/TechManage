# TechManage - User Management REST API

TechManage is a Spring Boot application that provides a REST API for managing users. It allows you to perform CRUD operations on user data with proper validation and error handling.

## Technologies Used

- Java 21
- Spring Boot 3.2.3
- Spring Data JPA
- H2 Database
- Lombok
- OpenAPI 3.0 (Swagger)
- Maven

## Features

- Complete user management (Create, Read, Update, Delete)
- Data validation
- OpenAPI documentation
- H2 in-memory database with sample data
- RESTful API design

## API Endpoints

The following endpoints are available:

| Method | URL                | Description          | Status Codes                |
|--------|-------------------|----------------------|----------------------------|
| GET    | /api/users        | Get all users        | 200 OK, 204 No Content     |
| GET    | /api/users/{id}   | Get user by ID       | 200 OK, 400 Bad Request    |
| POST   | /api/users        | Create a new user    | 200 OK, 400 Bad Request    |
| PUT    | /api/users/{id}   | Update a user        | 200 OK, 400 Bad Request    |
| DELETE | /api/users/{id}   | Delete a user        | 200 OK, 400 Bad Request    |

## Setup and Installation

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Clone the Repository

```bash
git clone https://github.com/MatheusTheBot/TechManage.git
cd TechManage
```

### Build the Application

```bash
mvn clean install
```

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Java

```bash
java -jar target/TechManage-0.0.1-SNAPSHOT.jar
```

The application will start on port 8080 by default.

## Accessing the API

Once the application is running, you can access:

- API endpoints at http://localhost:8080/api/users
- H2 Database Console at http://localhost:8080/h2-console

### H2 Console Configuration

- JDBC URL: `jdbc:h2:mem:techmanagedb`
- Username: `sa`
- Password: `password`

## Sample API Requests

### Get All Users

```bash
curl -X GET http://localhost:8080/api/users
```

### Get User by ID

```bash
curl -X GET http://localhost:8080/api/users/1
```

### Create a New User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+55 11 99999-9999",
    "birthDate": "1990-01-01",
    "userType": "ADMIN"
  }'
```

### Update a User

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Updated",
    "email": "john.updated@example.com",
    "phone": "+55 11 88888-8888",
    "birthDate": "1990-01-01",
    "userType": "EDITOR"
  }'
```

### Delete a User

```bash
curl -X DELETE http://localhost:8080/api/users/1
```

## Data Model

### User

| Field      | Type        | Description                        | Validation                   |
|------------|-------------|------------------------------------|------------------------------|
| id         | Long        | Unique identifier                   | Auto-generated              |
| fullName   | String      | User's full name                   | Not blank                    |
| email      | String      | User's email address               | Not blank, valid email, unique |
| phone      | String      | User's phone number                | Not blank                    |
| birthDate  | LocalDate   | User's date of birth               | Not null                     |
| userType   | EUserType   | User's role (ADMIN, EDITOR, VIEWER) | Not null                     |

## Testing

Run the unit tests with:

```bash
mvn test
```

## Project Structure

```
TechManage/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── matheus_henrique/
│   │   │       └── TechManage/
│   │   │           ├── Controllers/
│   │   │           ├── Enums/
│   │   │           ├── Infra/
│   │   │           │   └── Repository/
│   │   │           ├── Models/
│   │   │           └── Services/
│   │   └── resources/
│   │       ├── OpenApi/
│   │       │   └── MainApi.yaml
│   │       ├── Templates/
│   │       ├── application.properties
│   │       └── UsersFallback.json
│   └── test/
│       └── java/
│           └── matheus_henrique/
│               └── TechManage/
│                   ├── Controllers/
│                   ├── Services/
│                   └── TechManageApplicationTests.java
└── pom.xml
```

## Error Handling

The API returns a standardized error format:

```json
{
  "errors": ["Error message here"],
  "data": []
}
```

## Initialization

On startup, the application loads sample user data from either:
1. The Mockaroo API (if available)
2. A fallback JSON file included in the application

This ensures you always have data to work with when you start the application.

## License

[MIT License](LICENSE)

## Author

Matheus Henrique
