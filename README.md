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
- ✅ **Pagination & Sorting** - Efficient data retrieval with customizable pagination and sorting
- ✅ **RESTful API** - Well-designed REST API with OpenAPI/Swagger documentation
- ✅ **Web Interface** - Beautiful, responsive web UI built with Thymeleaf and Tailwind CSS
- ✅ **Exception Handling** - Comprehensive global exception handling with custom error responses
- ✅ **Custom Error Pages** - User-friendly HTML error pages (400, 403, 404, 500)
- ✅ **Multiple Database Support** - H2, PostgreSQL, MySQL, and MongoDB profiles
- ✅ **CORS Configuration** - Cross-origin resource sharing support
- ✅ **Logging** - Configurable logging with different levels
- ✅ **Health Monitoring** - Spring Boot Actuator for health checks and metrics
- ✅ **Code Coverage** - 90%+ test coverage with JaCoCo

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

### Example API Requests

#### Get All Todos (Paginated)

```bash
curl -X GET "http://localhost:8080/api/todos?page=0&size=10&sort=todo,asc"
```

#### Search Todos

```bash
curl -X GET "http://localhost:8080/api/todos/search?keyword=spring&status=PENDING&page=0&size=10"
```

#### Create Todo

```bash
curl -X POST "http://localhost:8080/api/todos" \
  -H "Content-Type: application/json" \
  -d '{
    "todo": "Learn Spring Boot",
    "description": "Complete Spring Boot tutorial",
    "status": "PENDING"
  }'
```

#### Update Todo

```bash
curl -X PUT "http://localhost:8080/api/todos/{id}" \
  -H "Content-Type: application/json" \
  -d '{
    "todo": "Learn Spring Boot - Updated",
    "description": "Complete Spring Boot tutorial - Updated",
    "status": "IN_PROGRESS"
  }'
```

#### Delete Todo

```bash
curl -X DELETE "http://localhost:8080/api/todos/{id}"
```

## 🌐 Web Interface

The application provides a beautiful, responsive web interface for managing todos without writing any API calls.

### Features

- **Todo List View** - View all todos with pagination
- **Create Todo** - Add new todos through a form
- **Edit Todo** - Update existing todos
- **Delete Todo** - Remove todos with confirmation
- **Search & Filter** - Search by keyword and filter by status
- **Responsive Design** - Works on desktop, tablet, and mobile devices

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
│   │       ├── application.yml        # Main configuration
│   │       ├── application-h2.yml    # H2 profile
│   │       ├── application-postgres.yml
│   │       ├── application-mysql.yml
│   │       └── application-mongodb.yml
│   └── test/
│       └── java/                      # Test classes
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

The project maintains **90%+ code coverage** using JaCoCo. Coverage is checked during the `verify` phase:

```bash
mvn verify
```

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

- Email: hieu@example.com

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Tailwind CSS for the beautiful UI framework
- OpenAPI/Swagger for API documentation
- All contributors and open-source libraries used in this project

## 📞 Support

For support, please open an issue in the GitHub repository or contact the maintainer.

---

**Happy Coding! 🚀**

