# Authentication Backend Implementation Guide

## Overview
This document explains the complete authentication system implemented for the Personality Analyzer application. The system includes user registration, login with password hashing, and MongoDB persistence.

## Architecture

### Backend Components

#### 1. **DTOs (Data Transfer Objects)**
Located in: `src/main/java/com/deadline/analyzer/dto/`

- **LoginRequest.java** - Request model for login
  - `email`: User email
  - `password`: User password

- **RegisterRequest.java** - Request model for registration
  - `fullName`: User's full name
  - `email`: User email
  - `password`: User password
  - `confirmPassword`: Password confirmation

- **AuthResponse.java** - Response model for both login and registration
  - `success`: Boolean indicating success/failure
  - `message`: Status message
  - `userId`: User MongoDB ID
  - `fullName`: User's full name
  - `email`: User email

#### 2. **User Model**
Located in: `src/main/java/com/deadline/analyzer/model/User.java`

MongoDB Document with fields:
- `id`: MongoDB ObjectId (automatically generated)
- `fullName`: User's full name
- `email`: User email (unique)
- `password`: BCrypt hashed password
- `personalityType`: Personality analysis type
- `averageDup`: Average deadline urgency perception
- `completionRate`: Task completion percentage
- `createdAt`: Account creation timestamp
- `updatedAt`: Last update timestamp

#### 3. **UserRepository**
Located in: `src/main/java/com/deadline/analyzer/repository/UserRepository.java`

MongoDB repository interface:
- `findByEmail(String email)`: Find user by email address
- Inherits standard CRUD operations from MongoRepository

#### 4. **UserService**
Located in: `src/main/java/com/deadline/analyzer/service/UserService.java`

Business logic layer with methods:
- `registerUser(RegisterRequest)`: Handle user registration
  - Validates password match
  - Checks for duplicate email
  - Validates email format
  - Validates password strength (minimum 6 characters)
  - Hashes password using BCrypt
  - Saves user to MongoDB
  - Returns AuthResponse with user details

- `loginUser(LoginRequest)`: Handle user login
  - Finds user by email
  - Verifies password using BCrypt
  - Updates last login timestamp
  - Returns AuthResponse with user details

- `getUserById(String userId)`: Retrieve user by ID
- `getUserByEmail(String email)`: Retrieve user by email
- `createUser(User)` and `updateUser(User)`: Utility methods

#### 5. **SecurityConfig**
Located in: `src/main/java/com/deadline/analyzer/config/SecurityConfig.java`

Spring configuration class:
- Provides `BCryptPasswordEncoder` bean for password hashing
- Used throughout the application for secure password handling

#### 6. **AuthController**
Located in: `src/main/java/com/deadline/analyzer/controller/AuthController.java`

REST API endpoints:

**POST /api/auth/register**
- Request body: RegisterRequest JSON
- Response: AuthResponse JSON
- HTTP Status:
  - 201 Created: Successful registration
  - 400 Bad Request: Validation error (duplicate email, password mismatch, etc.)

Example Request:
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "confirmPassword": "securePassword123"
}
```

Example Success Response (201):
```json
{
  "success": true,
  "message": "User registered successfully",
  "userId": "507f1f77bcf86cd799439011",
  "fullName": "John Doe",
  "email": "john@example.com"
}
```

Example Error Response (400):
```json
{
  "success": false,
  "message": "Email already registered",
  "userId": null,
  "fullName": null,
  "email": null
}
```

**POST /api/auth/login**
- Request body: LoginRequest JSON
- Response: AuthResponse JSON
- HTTP Status:
  - 200 OK: Successful login
  - 401 Unauthorized: Invalid credentials
  - 400 Bad Request: Validation error

Example Request:
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

Example Success Response (200):
```json
{
  "success": true,
  "message": "Login successful",
  "userId": "507f1f77bcf86cd799439011",
  "fullName": "John Doe",
  "email": "john@example.com"
}
```

Example Error Response (401):
```json
{
  "success": false,
  "message": "Invalid email or password",
  "userId": null,
  "fullName": null,
  "email": null
}
```

### Frontend Implementation

#### JavaScript API Calls (auth.js)

The frontend `src/main/resources/static/js/auth.js` has been updated with:

1. **Login Form Handler**
   - Validates email and password are not empty
   - Calls `POST /api/auth/login` with user credentials
   - On success:
     - Stores `userId`, `userFullName`, `userEmail`, `authToken` in localStorage
     - Optionally stores email for "Remember Me" functionality
     - Redirects to dashboard.html
   - On error: Displays error message to user

2. **Registration Form Handler**
   - Validates all required fields
   - Verifies passwords match
   - Ensures password minimum length (6 characters)
   - Verifies Terms of Service acceptance
   - Calls `POST /api/auth/register` with user data
   - On success:
     - Stores user data in localStorage
     - Redirects to dashboard.html
   - On error: Displays error message to user

3. **Alert Display Function**
   - Shows success, error, or warning messages
   - Auto-dismisses after 5 seconds
   - Supports Bootstrap alert styling

#### HTML Form Fields (login.html)

**Login Form:**
- `#email`: User email address
- `#password`: User password
- `#rememberMe`: Remember me checkbox
- Form ID: `#authForm`

**Registration Form:**
- `#fullName`: User's full name
- `#signupEmail`: User email address
- `#signupPassword`: User password
- `#confirmPassword`: Password confirmation
- `#agreeTerms`: Terms agreement checkbox
- Form ID: `#signupForm`

#### localStorage Usage

After successful login/registration, the following values are stored:
```javascript
localStorage.setItem('userId', data.userId);
localStorage.setItem('userFullName', data.fullName);
localStorage.setItem('userEmail', data.email);
localStorage.setItem('authToken', 'bearer_' + data.userId);
localStorage.setItem('rememberEmail', email); // Optional, if Remember Me checked
```

These values can be accessed in other pages:
```javascript
const userId = localStorage.getItem('userId');
const userName = localStorage.getItem('userFullName');
const userEmail = localStorage.getItem('userEmail');
const authToken = localStorage.getItem('authToken');
```

## Security Features

1. **Password Hashing**: Uses BCrypt with Spring Security
   - Passwords are never stored in plain text
   - Each password is salted and hashed
   - Verification uses secure comparison (prevents timing attacks)

2. **Input Validation**
   - Email format validation
   - Password strength validation (minimum 6 characters)
   - Password match verification
   - Duplicate email detection

3. **Error Messages**
   - Generic "Invalid email or password" message during login (prevents email enumeration attacks)
   - Specific messages for registration validation errors

4. **CORS Configuration**
   - AuthController allows cross-origin requests
   - Necessary for frontend to communicate with backend

## How to Run

### Prerequisites
- Java 17 or higher
- MongoDB running on localhost:27017 (or configured in application.properties)
- IntelliJ IDEA

### Steps

1. **Open Project in IntelliJ**
   - File > Open > Select pom.xml

2. **Reload Maven Dependencies**
   - Right-click project root > Maven > Reload Project

3. **Run Application**
   - Right-click DeadlineAnalyzerApplication.java
   - Select "Run 'DeadlineAnalyzerApplication.main()'"
   - Wait for "Tomcat started on port(s): 8080 (http)"

4. **Open in Browser**
   - Go to http://localhost:8080/login
   - Test registration by creating a new account
   - Test login with registered credentials

### MongoDB Setup

Ensure MongoDB is running:
```bash
# Windows (if using MongoDB Community)
mongod --dbpath "C:\Program Files\MongoDB\Server\data\db"

# Or start the MongoDB service if installed as a service
```

Configure connection in `src/main/resources/application.properties`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/personality-analyzer
spring.data.mongodb.database=personality-analyzer
```

## File Structure

```
src/
├── main/
│   ├── java/com/deadline/analyzer/
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java (NEW)
│   │   │   └── WebController.java
│   │   ├── dto/
│   │   │   ├── AuthResponse.java (NEW)
│   │   │   ├── LoginRequest.java (NEW)
│   │   │   └── RegisterRequest.java (NEW)
│   │   ├── model/
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   └── UserRepository.java
│   │   ├── service/
│   │   │   └── UserService.java (UPDATED)
│   │   └── DeadlineAnalyzerApplication.java
│   └── resources/
│       └── static/
│           └── js/
│               └── auth.js (UPDATED)
├── pom.xml (UPDATED)
└── README.md
```

## Testing the API

### Using cURL or Postman

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "password": "testPassword123",
    "confirmPassword": "testPassword123"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "testPassword123"
  }'
```

## Troubleshooting

1. **Port 8080 already in use**
   - Add to `application.properties`: `server.port=9090`
   - Or change port in Run Configuration

2. **MongoDB connection error**
   - Ensure MongoDB is running
   - Check connection string in application.properties
   - Check firewall settings

3. **CORS errors in browser console**
   - AuthController has @CrossOrigin annotation
   - Should allow all origins by default
   - If still issues, adjust CORS settings in SecurityConfig

4. **Password hashing fails**
   - Ensure BCryptPasswordEncoder bean is initialized
   - Check SecurityConfig.java exists and is properly configured
   - Verify maven dependencies are downloaded

## Future Enhancements

1. Add JWT (JSON Web Token) authentication for stateless API
2. Implement email verification for new accounts
3. Add password reset functionality
4. Implement role-based access control (RBAC)
5. Add rate limiting for login attempts
6. Implement session management
7. Add OAuth2 social login integration

