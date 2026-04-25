Project Description
A microservice system for automating insurance processes. The project is a backend solution for insurance companies, providing an API for managing clients, vehicles, drivers, and insurance policies.
The main problem addressed by the system is the lengthy process of issuing insurance policies, the lack of 24/7 support, and the high error rate in manual premium calculations.
The project is designed for insurance companies, insurance agents, and car owners.

Functionality
User registration and authentication with JWT tokens
Password recovery via email
Client management (CRUD operations)
Car management with support for various vehicle types
Driver management
Creation of three types of insurance policies: OSAGO, DSAGO, and CASCO
Automatic insurance premium calculation
Insurance policy generation in PDF format
Chatbot with user intent recognition
API documentation via Swagger

Architecture
The system consists of three microservices:
  Auth Service (port 4005) - authentication and user management
  Insurance Service (port 4000) - core business logic
  API Gateway (port 4004) - single entry point
Installation and Runtime
Requirements:
  Docker and Docker Compose
  Java 17 
  Maven
  PostgreSQL 

Running via Docker Compose:
git clone https://github.com/yourusername/insurance-system.git
cd insurance-system
docker-compose up -d

After startup, the services will be available at the following addresses:
API Gateway: http://localhost:4004
Auth Service: http://localhost:4005
Insurance Service: http://localhost:4000

Running locally without Docker:
  cd auth-service
  mvn spring-boot:run

  cd insurance-service
  mvn spring-boot:run

  cd api-gateway
  mvn spring-boot:run

Usage
API documentation is available via Swagger UI:
Insurance Service API: http://localhost:4004/insurance-api-docs
Auth Service API: http://localhost:4004/auth-api-docs

Example requests:
User registration:
POST /register
{
"email": "user@example.com",
"password": "password123",
"confirmPassword": "password123",
"role": "USER"
}

Login:
POST /login
{
"email": "user@example.com",
"password": "password123"
}

Create a client:
POST /api/clients
Authorization: Bearer <token>
{
"fullName": "Ivan Ivanov",
"dateOfBirth": "1990-01-01",
"phoneNumber": "+996555123456",
"passportNumber": "AN1234567",
"pin": "12345678901234",
"address": "Bishkek, Chuikov St. 123"
}

Creating an insurance policy:
POST /api/policies
Authorization: Bearer <token>
{
"policyNumber": "POL001",
"policyType": "OSAGO",
"startDate": "2024-01-01",
"endDate": "2024-12-31",
"status": "ACTIVE",
"vehicleOwner": {"id": "client-id"},
"insuredCar": {"id": "car-id"}
}

Download your policy in PDF:
GET /api/policies/{policyNumber}/download

Send a message to the chatbot:
POST /api/chat/message
Authorization: Bearer <token>
{
"sessionId": "session-123",
"message": "Show me my policy POL001"
}

Project Structure
insurance-system/
├── auth-service/ # Authentication Service
├── insurance-service/ # Main Service
├── api-gateway/ # API Gateway
├── insurance-frontend/ # Frontend 
└── docker-compose.yml # Docker Compose
