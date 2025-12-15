# Deployment Directory

This directory contains all Docker and deployment-related files for the Spring Boot Todo Management Application.

## Files

- **Dockerfile** - Multi-stage Docker build configuration
- **.dockerignore** - Files to exclude from Docker build context
- **docker-compose.yml** - Full stack with all databases (PostgreSQL, MySQL, MongoDB)
- **docker-compose.dev.yml** - Development mode with H2 in-memory database
- **docker-compose.prod.yml** - Production mode with PostgreSQL

## Quick Start

### Build Docker Image

From the project root directory:

```bash
docker build -f deployment/Dockerfile -t spring-boot-todo-app .
```

### Run with Docker Compose

#### Option 1: From deployment folder

```bash
cd deployment
docker-compose up -d
```

#### Option 2: From project root

```bash
docker-compose -f deployment/docker-compose.yml up -d
```

### Development Mode (H2)

```bash
cd deployment
docker-compose -f docker-compose.dev.yml up
```

### Production Mode (PostgreSQL)

```bash
cd deployment
docker-compose -f docker-compose.prod.yml up -d
```

## Important Notes

- **Build Context**: The Docker build context must be the project root directory (parent of `deployment/`)
- **Dockerfile Path**: When building, use `-f deployment/Dockerfile` to specify the Dockerfile location
- **Docker Compose**: All docker-compose files reference the parent directory as build context (`context: ..`)

## Environment Variables

Create a `.env` file in the `deployment` folder (or use environment variables):

```bash
# Application
SPRING_PROFILES_ACTIVE=postgres
PORT=8080

# Database
DB_HOST=postgres
DB_PORT=5432
DB_NAME=todo
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

## Database Services

The `docker-compose.yml` includes:
- **PostgreSQL** - Port 5432
- **MySQL** - Port 3306
- **MongoDB** - Port 27017

All databases use named volumes for data persistence.

## Health Checks

All services include health checks:
- Application: `http://localhost:8080/actuator/health`
- PostgreSQL: `pg_isready`
- MySQL: `mysqladmin ping`
- MongoDB: `mongosh ping`

## Troubleshooting

### Build fails with "pom.xml not found"
- Ensure you're running the build command from the project root directory
- Use `-f deployment/Dockerfile` to specify the Dockerfile path

### Docker Compose can't find Dockerfile
- Ensure the `context: ..` is set correctly in docker-compose files
- Run docker-compose from the deployment folder or use `-f` flag with full path

### Database connection issues
- Check that database services are healthy: `docker-compose ps`
- Verify environment variables are set correctly
- Check network connectivity: `docker network ls`

