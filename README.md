# Spring Boot Todo Management Application

A modern, full-stack Spring Boot application for managing todos with a beautiful web interface and comprehensive REST API. This application demonstrates best practices in Spring Boot development, including exception handling, custom error pages, API documentation, and comprehensive testing.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen)
![Java](https://img.shields.io/badge/Java-21-orange)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Web Interface](#-web-interface)
- [Screenshots](#-screenshots)
- [Project Structure](#-project-structure)
- [Testing](#-testing)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### Core Features
- ✅ **Full CRUD Operations** - Create, Read, Update, and Delete todos
- ✅ **Advanced Search** - Search todos by keyword and filter by status
- ✅ **Smart Pagination** - Efficient pagination with intelligent page number display (shows current page ±2, first, last, and ellipsis)
- ✅ **Pagination & Sorting** - Efficient data retrieval with customizable pagination and sorting
- ✅ **RESTful API** - Well-designed REST API with OpenAPI/Swagger documentation
- ✅ **Web Interface** - Beautiful, responsive web UI built with Thymeleaf and Tailwind CSS
- ✅ **Dark Theme Support** - Toggle between light and dark themes with persistent preference
- ✅ **CSV Data Loading** - Load initial data from CSV file (10,000 records supported)
- ✅ **Exception Handling** - Comprehensive global exception handling with custom error responses
- ✅ **Custom Error Pages** - User-friendly HTML error pages (400, 403, 404, 500)
- ✅ **Multiple Database Support** - H2, PostgreSQL, MySQL, and MongoDB profiles
- ✅ **CORS Configuration** - Cross-origin resource sharing support
- ✅ **Logging** - Configurable logging with different levels
- ✅ **Health Monitoring** - Spring Boot Actuator for health checks and metrics
- ✅ **Code Coverage** - 50%+ test coverage with JaCoCo

### Technical Features
- UUID-based primary keys
- DTO pattern for data transfer
- Service layer with business logic separation
- Repository pattern with Spring Data JPA
- Global exception handler
- Request validation
- API versioning ready
- Comprehensive unit and integration tests

## 🛠 Technology Stack

### Backend
- **Spring Boot 3.5.9** - Application framework
- **Java 21** - Programming language
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework
- **Lombok** - Boilerplate code reduction

### Frontend
- **Thymeleaf** - Server-side templating
- **Tailwind CSS** - Utility-first CSS framework
- **JavaScript** - Client-side interactivity

### Database
- **H2** (default) - In-memory database for development
- **PostgreSQL** - Production-ready relational database
- **MySQL** - Alternative relational database
- **MongoDB** - NoSQL database support

### Tools & Libraries
- **OpenAPI/Swagger** - API documentation
- **Spring Boot Actuator** - Application monitoring
- **JaCoCo** - Code coverage analysis
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** or higher
- **Maven 3.6+** or higher
- **Git** (for cloning the repository)
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Optional (for specific database profiles)
- **PostgreSQL 12+** (for PostgreSQL profile)
- **MySQL 8+** (for MySQL profile)
- **MongoDB 4+** (for MongoDB profile)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring-boot-hello-world
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run Tests (Optional)

```bash
mvn test
```

### 4. Initial Data Setup

The application automatically loads initial data from a CSV file on first startup:

- **Production**: `src/main/resources/data/todos.csv` (10,000 records)
- **Tests**: `src/test/resources/data/todos.csv` (8 records)

The CSV format is:
```csv
todo,description,status
Complete Spring Boot project,Finish implementing all features,IN_PROGRESS
Review code changes,Go through the pull request and provide feedback,PENDING
...
```

**Status values**: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`

If the CSV file is not found, the application will generate sample data automatically. Data is loaded in batches of 1,000 records for optimal performance.

## ⚙️ Configuration

### Application Configuration

The application uses Spring Profiles to support different database configurations. The default profile is `h2` (in-memory database).

#### Default Configuration (H2 - In-Memory)

No additional configuration needed. The application will use H2 database by default.

#### PostgreSQL Profile

1. Set the active profile:
```bash
export SPRING_PROFILES_ACTIVE=postgres
# Or on Windows:
set SPRING_PROFILES_ACTIVE=postgres
```

2. Configure database connection in `application-postgres.yml` or via environment variables:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/todo
    username: postgres
    password: postgres
```

#### MySQL Profile

1. Set the active profile:
```bash
export SPRING_PROFILES_ACTIVE=mysql
```

2. Configure database connection in `application-mysql.yml` or via environment variables.

#### MongoDB Profile

1. Set the active profile:
```bash
export SPRING_PROFILES_ACTIVE=mongodb
```

2. Configure MongoDB connection in `application-mongodb.yml` or via environment variables.

### Environment Variables

You can override configuration using environment variables:

```bash
# Server port
export PORT=8080

# Database configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=todo
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

# Spring profile
export SPRING_PROFILES_ACTIVE=h2
```

## 🏃 Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Java

```bash
java -jar target/spring-boot-hello-world-0.0.1-SNAPSHOT.jar
```

### Using IDE

Run the `SpringBootHelloWorldApplication` class directly from your IDE.

### Using Docker

#### Prerequisites

- **Docker** 20.10+ installed
- **Docker Compose** 2.0+ installed (optional, for docker-compose)

#### Build Docker Image

```bash
# Build from project root (deployment folder contains Dockerfile)
docker build -f deployment/Dockerfile -t spring-boot-todo-app .
```

#### Run with Docker (H2 - In-Memory Database)

```bash
docker run -p 8080:8080 spring-boot-todo-app
```

#### Run with Docker Compose (All Databases)

```bash
# Navigate to deployment folder or use -f flag
cd deployment
docker-compose up -d

# Or from project root:
docker-compose -f deployment/docker-compose.yml up -d

# View logs
docker-compose -f deployment/docker-compose.yml logs -f app

# Stop all services
docker-compose -f deployment/docker-compose.yml down

# Stop and remove volumes
docker-compose -f deployment/docker-compose.yml down -v
```

#### Development Mode (H2 Only)

```bash
# From deployment folder:
cd deployment
docker-compose -f docker-compose.dev.yml up

# Or from project root:
docker-compose -f deployment/docker-compose.dev.yml up
```

#### Production Mode (PostgreSQL)

```bash
# Copy and configure environment variables (if .env.example exists)
# cp deployment/.env.example deployment/.env

# Edit .env file with your production values
# Then run from deployment folder:
cd deployment
docker-compose -f docker-compose.prod.yml up -d

# Or from project root:
docker-compose -f deployment/docker-compose.prod.yml up -d
```

#### Docker Commands

```bash
# Build image (from project root)
docker build -f deployment/Dockerfile -t spring-boot-todo-app .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=postgres \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=todo \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  spring-boot-todo-app

# View running containers
docker ps

# View logs
docker logs spring-boot-todo-app

# Stop container
docker stop spring-boot-todo-app

# Remove container
docker rm spring-boot-todo-app
```

### Access the Application

Once started, the application will be available at:

- **Web Interface**: http://localhost:8080
- **REST API**: http://localhost:8080/api/todos
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

## 📚 API Documentation

### Swagger UI

Interactive API documentation is available at:
```
http://localhost:8080/swagger-ui.html
```

### API Endpoints

#### Todo Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/todos` | Get all todos (with pagination & sorting) |
| GET | `/api/todos/search` | Search todos by keyword/status |
| GET | `/api/todos/{id}` | Get todo by ID |
| POST | `/api/todos` | Create a new todo |
| PUT | `/api/todos/{id}` | Update a todo |
| DELETE | `/api/todos/{id}` | Delete a todo |

#### Utility Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/ping` | Health check endpoint |

### cURL Commands for All APIs

All API endpoints can be tested using cURL commands. Replace `{id}` with an actual UUID when needed.

#### 1. Ping Endpoint

**Health Check**
```bash
curl -X GET "http://localhost:8080/ping"
```

**Expected Response:**
```
pong
```

---

#### 2. Get All Todos

**Basic Request (Default Pagination)**
```bash
curl -X GET "http://localhost:8080/api/todos"
```

**With Pagination**
```bash
curl -X GET "http://localhost:8080/api/todos?page=0&size=10"
```

**With Sorting (Ascending)**
```bash
curl -X GET "http://localhost:8080/api/todos?page=0&size=10&sort=todo,asc"
```

**With Sorting (Descending)**
```bash
curl -X GET "http://localhost:8080/api/todos?page=0&size=10&sort=todo,desc"
```

**Sort by Status**
```bash
curl -X GET "http://localhost:8080/api/todos?page=0&size=10&sort=status,asc"
```

**Sort by ID (Newest First)**
```bash
curl -X GET "http://localhost:8080/api/todos?page=0&size=10&sort=id,desc"
```

**Pretty Print JSON Response**
```bash
curl -X GET "http://localhost:8080/api/todos?page=0&size=10" | json_pp
```

---

#### 3. Search Todos

**Search by Keyword Only**
```bash
curl -X GET "http://localhost:8080/api/todos/search?keyword=spring"
```

**Search by Status Only**
```bash
curl -X GET "http://localhost:8080/api/todos/search?status=PENDING"
```

**Search by Keyword and Status**
```bash
curl -X GET "http://localhost:8080/api/todos/search?keyword=spring&status=PENDING"
```

**Search with Pagination**
```bash
curl -X GET "http://localhost:8080/api/todos/search?keyword=spring&status=PENDING&page=0&size=10"
```

**Search with Sorting**
```bash
curl -X GET "http://localhost:8080/api/todos/search?keyword=spring&status=PENDING&page=0&size=10&sort=todo,asc"
```

**Available Status Values:**
- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`
- `CANCELLED`

---

#### 4. Get Todo by ID

**Get Single Todo**
```bash
curl -X GET "http://localhost:8080/api/todos/123e4567-e89b-12d3-a456-426614174000"
```

**Replace `123e4567-e89b-12d3-a456-426614174000` with an actual UUID from your database.**

**Example with Pretty Print:**
```bash
curl -X GET "http://localhost:8080/api/todos/123e4567-e89b-12d3-a456-426614174000" | json_pp
```

---

#### 5. Create Todo

**Create Todo with All Fields**
```bash
curl -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{
    "todo": "Learn Spring Boot",
    "description": "Complete Spring Boot tutorial and build a REST API",
    "status": "PENDING"
  }'
```

**Create Todo with Minimal Fields (Status defaults to PENDING)**
```bash
curl -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{
    "todo": "Learn Spring Boot"
  }'
```

**Create Todo with Different Status**
```bash
curl -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{
    "todo": "Deploy Application",
    "description": "Deploy Spring Boot app to production",
    "status": "IN_PROGRESS"
  }'
```

**Create Todo - All Status Options:**
```bash
# PENDING
curl -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{"todo": "Task 1", "status": "PENDING"}'

# IN_PROGRESS
curl -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{"todo": "Task 2", "status": "IN_PROGRESS"}'

# COMPLETED
curl -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{"todo": "Task 3", "status": "COMPLETED"}'

# CANCELLED
curl -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{"todo": "Task 4", "status": "CANCELLED"}'
```

---

#### 6. Update Todo

**Update All Fields**
```bash
curl -X PUT "http://localhost:8080/api/todos/123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "todo": "Learn Spring Boot - Updated",
    "description": "Complete Spring Boot tutorial - Updated description",
    "status": "IN_PROGRESS"
  }'
```

**Update Only Title**
```bash
curl -X PUT "http://localhost:8080/api/todos/123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "todo": "Updated Todo Title"
  }'
```

**Update Only Description**
```bash
curl -X PUT "http://localhost:8080/api/todos/123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Updated description only"
  }'
```

**Update Only Status**
```bash
curl -X PUT "http://localhost:8080/api/todos/123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED"
  }'
```

**Mark Todo as Completed**
```bash
curl -X PUT "http://localhost:8080/api/todos/123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}'
```

---

#### 7. Delete Todo

**Delete Todo by ID**
```bash
curl -X DELETE "http://localhost:8080/api/todos/123e4567-e89b-12d3-a456-426614174000"
```

**Delete with Verbose Output**
```bash
curl -v -X DELETE "http://localhost:8080/api/todos/123e4567-e89b-12d3-a456-426614174000"
```

---

### Complete Workflow Example

Here's a complete workflow example from creating to deleting a todo:

```bash
# 1. Create a new todo
RESPONSE=$(curl -s -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{
    "todo": "Test Todo",
    "description": "This is a test todo",
    "status": "PENDING"
  }')

# Extract the ID from the response (requires jq)
TODO_ID=$(echo $RESPONSE | jq -r '.id')
echo "Created Todo ID: $TODO_ID"

# 2. Get the created todo
curl -X GET "http://localhost:8080/api/todos/$TODO_ID" | json_pp

# 3. Update the todo
curl -X PUT "http://localhost:8080/api/todos/$TODO_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_PROGRESS"
  }'

# 4. Mark as completed
curl -X PUT "http://localhost:8080/api/todos/$TODO_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED"
  }'

# 5. Delete the todo
curl -X DELETE "http://localhost:8080/api/todos/$TODO_ID"
```

---

### Response Examples

#### Success Response (Get All Todos)
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "todo": "Learn Spring Boot",
      "description": "Complete Spring Boot tutorial",
      "status": "PENDING"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

#### Success Response (Create Todo)
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "todo": "Learn Spring Boot",
  "description": "Complete Spring Boot tutorial",
  "status": "PENDING"
}
```

#### Error Response (404 Not Found)
```json
{
  "timestamp": "2024-12-15T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Todo not found with id: '123e4567-e89b-12d3-a456-426614174000'",
  "path": "/api/todos/123e4567-e89b-12d3-a456-426614174000"
}
```

#### Error Response (400 Bad Request - Validation Error)
```json
{
  "timestamp": "2024-12-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input provided",
  "path": "/api/todos",
  "details": [
    "todo: Todo title is required"
  ]
}
```

---

### Tips for Using cURL

1. **Pretty Print JSON**: Pipe output to `json_pp` or `jq`:
   ```bash
   curl -X GET "http://localhost:8080/api/todos" | json_pp
   curl -X GET "http://localhost:8080/api/todos" | jq
   ```

2. **Save Response to File**:
   ```bash
   curl -X GET "http://localhost:8080/api/todos" -o response.json
   ```

3. **Include Headers in Output**:
   ```bash
   curl -i -X GET "http://localhost:8080/api/todos"
   ```

4. **Verbose Output (Debugging)**:
   ```bash
   curl -v -X GET "http://localhost:8080/api/todos"
   ```

5. **Follow Redirects**:
   ```bash
   curl -L -X GET "http://localhost:8080/api/todos"
   ```

6. **Set Timeout**:
   ```bash
   curl --max-time 10 -X GET "http://localhost:8080/api/todos"
   ```

---

### Actuator Endpoints

Spring Boot Actuator provides additional endpoints for monitoring and managing the application.

#### Health Check
```bash
curl -X GET "http://localhost:8080/actuator/health"
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

#### Application Info
```bash
curl -X GET "http://localhost:8080/actuator/info"
```

#### Metrics
```bash
curl -X GET "http://localhost:8080/actuator/metrics"
```

**Get Specific Metric:**
```bash
curl -X GET "http://localhost:8080/actuator/metrics/jvm.memory.used"
```

#### All Available Endpoints
```bash
curl -X GET "http://localhost:8080/actuator"
```

## 🌐 Web Interface

The application provides a beautiful, responsive web interface for managing todos without writing any API calls.

### Features

- **Todo List View** - View all todos with smart pagination (shows limited page numbers with ellipsis)
- **Create Todo** - Add new todos through a form
- **Edit Todo** - Update existing todos
- **Delete Todo** - Remove todos with confirmation
- **Search & Filter** - Search by keyword and filter by status
- **Dark Theme** - Toggle between light and dark themes (preference saved in browser)
- **Responsive Design** - Works on desktop, tablet, and mobile devices
- **Smart Pagination** - Intelligent page number display showing:
  - Current page and 2 pages on each side
  - First and last pages when needed
  - Ellipsis (...) for gaps between page ranges

### Access Web Interface

Navigate to: http://localhost:8080

## 📸 Screenshots

### Home Page
![Home Page](docs/screenshots/home-page.png)
*Welcome page with navigation to todos*

### Todo List View
![Todo List](docs/screenshots/todo-list.png)
*View all todos with pagination, sorting, and filtering options*

### Create Todo Form
![Create Todo](docs/screenshots/create-todo.png)
*Form to create a new todo with validation*

### Edit Todo Form
![Edit Todo](docs/screenshots/edit-todo.png)
*Form to update an existing todo*

### Search Results
![Search](docs/screenshots/search-results.png)
*Search results with keyword and status filters*

### Error Pages
![404 Error](docs/screenshots/404-error.png)
*Custom 404 Not Found error page*

![500 Error](docs/screenshots/500-error.png)
*Custom 500 Internal Server Error page*

### Swagger UI
![Swagger UI](docs/screenshots/swagger-ui.png)
*Interactive API documentation with Swagger UI*

### API Response Example
![API Response](docs/screenshots/api-response.png)
*Example JSON response from REST API*

> **Note**: Screenshots should be placed in the `docs/screenshots/` directory. Replace the placeholder paths above with actual screenshot files.

## 📁 Project Structure

```
spring-boot-hello-world/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── dev/hieu/springboothelloworld/
│   │   │       ├── configuration/      # Configuration classes
│   │   │       ├── domain/            # Entity classes
│   │   │       ├── dto/               # Data Transfer Objects
│   │   │       ├── exception/         # Exception handling
│   │   │       ├── repository/        # Data access layer
│   │   │       ├── service/           # Business logic layer
│   │   │       ├── web/
│   │   │       │   ├── api/           # REST API controllers
│   │   │       │   └── controller/   # Web controllers (Thymeleaf)
│   │   │       └── SpringBootHelloWorldApplication.java
│   │   └── resources/
│   │       ├── templates/             # Thymeleaf templates
│   │       │   ├── error/             # Error pages
│   │       │   └── todos/             # Todo pages
│   │       ├── data/                  # Initial data files
│   │       │   └── todos.csv         # CSV file with 10,000 records
│   │       ├── application.yml        # Main configuration
│   │       ├── application-h2.yml    # H2 profile
│   │       ├── application-postgres.yml
│   │       ├── application-mysql.yml
│   │       └── application-mongodb.yml
│   └── test/
│       ├── java/                      # Test classes
│       └── resources/
│           ├── data/                  # Test data files
│           │   └── todos.csv         # Test CSV (8 records)
│           └── test-data/            # JSON test data
├── deployment/                        # Docker and deployment files
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── docker-compose.dev.yml
│   └── docker-compose.prod.yml
├── pom.xml                             # Maven configuration
└── README.md                           # This file
```

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Tests with Coverage

```bash
mvn test jacoco:report
```

### View Coverage Report

After running tests, open the coverage report:
```
target/site/jacoco/index.html
```

### Coverage Requirements

The project maintains **50%+ code coverage** using JaCoCo. Coverage is checked during the `verify` phase:

```bash
mvn verify
```

**Coverage Exclusions:**
- Domain entities (`**/domain/**`)
- Exception classes (`**/exception/**`)
- Configuration classes (`**/configuration/**`)

These exclusions are configured in `pom.xml` to focus coverage on business logic and API layers.

### Test Structure

- **Unit Tests** - Service layer and business logic
- **Integration Tests** - API endpoints and controllers
- **Web Tests** - Thymeleaf controllers and views

## 🔧 Development

### Adding New Features

1. Create feature branch: `git checkout -b feature/new-feature`
2. Implement changes
3. Add tests
4. Ensure coverage meets 90% threshold
5. Submit pull request

### Code Style

- Follow Java naming conventions
- Use Lombok annotations where appropriate
- Add JavaDoc comments for public APIs
- Follow Spring Boot best practices

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Contribution Guidelines

- Write clear commit messages
- Add tests for new features
- Ensure all tests pass
- Maintain code coverage above 90%
- Update documentation as needed

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Hieu**

- Email: hieuprogrammer@gmail.com

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Tailwind CSS for the beautiful UI framework
- OpenAPI/Swagger for API documentation
- All contributors and open-source libraries used in this project

## 📞 Support

For support, please open an issue in the GitHub repository or contact the maintainer.

---

**Happy Coding! 🚀**
