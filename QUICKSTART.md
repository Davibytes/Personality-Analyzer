# Authentication Implementation - Quick Start Guide

## What Was Implemented

Complete authentication system with secure password hashing, email validation, and MongoDB persistence.

### New Files Created:

1. **DTOs** (Data Transfer Objects):
   - `src/main/java/com/deadline/analyzer/dto/LoginRequest.java`
   - `src/main/java/com/deadline/analyzer/dto/RegisterRequest.java`
   - `src/main/java/com/deadline/analyzer/dto/AuthResponse.java`

2. **Configuration**:
   - `src/main/java/com/deadline/analyzer/config/SecurityConfig.java` - Provides BCrypt encoder bean

3. **Controller**:
   - `src/main/java/com/deadline/analyzer/controller/AuthController.java` - REST API endpoints

### Files Modified:

1. **pom.xml** - Added Spring Security dependency for BCrypt
2. **src/main/java/com/deadline/analyzer/service/UserService.java** - Added authentication logic
3. **src/main/resources/static/js/auth.js** - Updated to call backend APIs

## API Endpoints

### POST /api/auth/register
Register a new user

**Request:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "confirmPassword": "password123"
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "userId": "507f1f77bcf86cd799439011",
  "fullName": "John Doe",
  "email": "john@example.com"
}
```

### POST /api/auth/login
Authenticate user

**Request:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "userId": "507f1f77bcf86cd799439011",
  "fullName": "John Doe",
  "email": "john@example.com"
}
```

## How to Run in IntelliJ IDEA

1. **Open Project**
   - File > Open > Navigate to `pom.xml`

2. **Let Maven Download Dependencies**
   - Wait for indexing to complete (bottom right corner)
   - Or right-click project > Maven > Reload Project

3. **Start the Application**
   - Open `src/main/java/com/deadline/analyzer/DeadlineAnalyzerApplication.java`
   - Click the green play button or right-click > Run 'DeadlineAnalyzerApplication.main()'
   - Wait for: "Tomcat started on port(s): 8080 (http)"

4. **Test the Login Page**
   - Open browser: http://localhost:8080/login
   - Click "Sign Up" to create an account
   - Fill in form:
     - Full Name: Your name
     - Email: your@email.com
     - Password: (min 6 characters)
     - Confirm Password: Must match
     - Agree to Terms: Check the checkbox
   - Click "Create Account"
   - If successful, you'll be redirected to dashboard

5. **Test Login**
   - Click "Sign In"
   - Enter email and password from registration
   - Click "Sign In"
   - You'll be redirected to dashboard

## MongoDB Setup

Make sure MongoDB is running before starting the application.

**Windows:**
```powershell
# Start MongoDB service (if installed as service)
net start MongoDB

# Or run mongod directly
mongod --dbpath "C:\Program Files\MongoDB\Server\data\db"
```

**Linux/Mac:**
```bash
brew services start mongodb-community
# or
mongod
```

## Security Features

✅ **Password Hashing**: Uses BCrypt - passwords are never stored in plain text

✅ **Input Validation**:
- Email format validation
- Password strength (minimum 6 characters)
- Password match verification
- Duplicate email prevention

✅ **Error Handling**: Proper HTTP status codes and user-friendly error messages

✅ **localStorage Usage**: User data stored client-side for session management

## Debugging Tips

If you encounter issues:

1. **Check the Console in IntelliJ**
   - Look for error messages when running the app
   - Check MongoDB connection errors

2. **Check Browser Console**
   - Press F12 in browser
   - Look for JavaScript errors or failed API calls
   - Check Network tab to see API responses

3. **Check MongoDB**
   - Ensure MongoDB is running
   - You can view data using MongoDB Compass or `mongosh` CLI

4. **Port Issues**
   - If 8080 is already in use, add to `application.properties`:
     ```properties
     server.port=9090
     ```

## Frontend Integration

The login page (`login.html`) now sends requests to:
- `POST /api/auth/register` for new account creation
- `POST /api/auth/login` for user authentication

Upon successful authentication, the frontend stores:
```javascript
localStorage.setItem('userId', userId);
localStorage.setItem('userFullName', fullName);
localStorage.setItem('userEmail', email);
localStorage.setItem('authToken', authToken);
```

You can use these values in other pages to verify user is logged in.

## Project Structure

```
Personality-Analyzer/
├── src/main/java/com/deadline/analyzer/
│   ├── config/
│   │   └── SecurityConfig.java (password encoder bean)
│   ├── controller/
│   │   ├── AuthController.java (NEW - login & register endpoints)
│   │   └── WebController.java
│   ├── dto/
│   │   ├── LoginRequest.java (NEW)
│   │   ├── RegisterRequest.java (NEW)
│   │   └── AuthResponse.java (NEW)
│   ├── model/
│   │   └── User.java (MongoDB document)
│   ├── repository/
│   │   └── UserRepository.java
│   ├── service/
│   │   └── UserService.java (UPDATED with auth methods)
│   └── DeadlineAnalyzerApplication.java
├── src/main/resources/
│   ├── static/
│   │   └── js/
│   │       └── auth.js (UPDATED with API calls)
│   └── application.properties
└── pom.xml (UPDATED with Spring Security dependency)
```

## Database Schema (MongoDB)

Users are stored in `personality-analyzer` database, `users` collection:

```json
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "$2a$10$...", // BCrypt hashed
  "personalityType": "UNANALYZED",
  "averageDup": 0.0,
  "completionRate": 0,
  "createdAt": 1704067200000,
  "updatedAt": 1704067200000
}
```

## Next Steps

After authentication is working, you can:

1. **Add JWT tokens** for stateless API authentication
2. **Protect dashboard routes** - Check if user is logged in before showing pages
3. **Add email verification** for new registrations
4. **Implement logout** functionality
5. **Add password reset** feature
6. **Extend User model** with additional fields as needed

## Support

For detailed documentation, see `AUTHENTICATION.md` in project root.

All code follows Spring Boot best practices and uses:
- Lombok for reducing boilerplate
- Spring Data MongoDB for database operations
- BCrypt for password security
- REST API design principles

