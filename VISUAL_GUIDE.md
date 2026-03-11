# Authentication System - Visual Flow Diagram

## 1. Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        FRONTEND (Browser)                        │
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ login.html - Login/Registration Form                  │     │
│  ├────────────────────────────────────────────────────────┤     │
│  │  • Login Form (email, password)                        │     │
│  │  • Registration Form (fullName, email, password)       │     │
│  │  • Form validation & error display                     │     │
│  └────────────────────────────────────────────────────────┘     │
│                         ↑ / ↓                                     │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ auth.js - Form Handler                                │     │
│  ├────────────────────────────────────────────────────────┤     │
│  │  • Collect form data                                   │     │
│  │  • Validate inputs                                     │     │
│  │  • Call backend APIs                                   │     │
│  │  • Handle responses                                    │     │
│  │  • Store in localStorage                               │     │
│  │  • Redirect to dashboard                               │     │
│  └────────────────────────────────────────────────────────┘     │
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ auth-utils.js - Authentication Utilities               │     │
│  ├────────────────────────────────────────────────────────┤     │
│  │  • isUserLoggedIn() - Check authentication             │     │
│  │  • requireLogin() - Protect pages                      │     │
│  │  • getCurrentUser() - Get user data                    │     │
│  │  • logout() - Clear session                            │     │
│  │  • authenticatedFetch() - Make API calls               │     │
│  └────────────────────────────────────────────────────────┘     │
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ localStorage - Session Storage                         │     │
│  ├────────────────────────────────────────────────────────┤     │
│  │  • userId, userFullName, userEmail, authToken          │     │
│  │  • Persists across page refreshes                       │     │
│  │  • Cleared on logout                                    │     │
│  └────────────────────────────────────────────────────────┘     │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
                         ↑ / ↓ (HTTP REST)
┌─────────────────────────────────────────────────────────────────┐
│                   BACKEND (Spring Boot)                          │
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ AuthController - REST API Endpoints                   │     │
│  ├────────────────────────────────────────────────────────┤     │
│  │  POST /api/auth/register                              │     │
│  │    • Validate request                                  │     │
│  │    • Call UserService.registerUser()                   │     │
│  │    • Return AuthResponse                               │     │
│  │                                                        │     │
│  │  POST /api/auth/login                                 │     │
│  │    • Validate request                                  │     │
│  │    • Call UserService.loginUser()                      │     │
│  │    • Return AuthResponse                               │     │
│  └────────────────────────────────────────────────────────┘     │
│                         ↑ / ↓                                     │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ UserService - Business Logic                          │     │
│  ├────────────────────────────────────────────────────────┤     │
│  │ registerUser(RegisterRequest):                         │     │
│  │  1. Validate passwords match                           │     │
│  │  2. Check for duplicate email                          │     │
│  │  3. Validate email format                              │     │
│  │  4. Validate password strength (min 6 chars)           │     │
│  │  5. Hash password with BCryptPasswordEncoder           │     │
│  │  6. Save User to MongoDB                               │     │
│  │  7. Return AuthResponse                                │     │
│  │                                                        │     │
│  │ loginUser(LoginRequest):                               │     │
│  │  1. Find user by email (UserRepository)                │     │
│  │  2. Verify password with BCrypt                        │     │
│  │  3. Update lastLogin timestamp                         │     │
│  │  4. Return AuthResponse with user data                 │     │
│  └────────────────────────────────────────────────────────┘     │
│                         ↑ / ↓                                     │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ UserRepository - Data Access                          │     │
│  ├────────────────────────────────────────────────────────┤     │
│  │  • Extends MongoRepository<User, String>               │     │
│  │  • findByEmail(String email)                           │     │
│  │  • save(User user) - inherited                         │     │
│  │  • findById(String id) - inherited                     │     │
│  └────────────────────────────────────────────────────────┘     │
│                         ↑ / ↓                                     │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ SecurityConfig - Configuration                        │     │
│  ├────────────────────────────────────────────────────────┤     │
│  │  @Bean                                                 │     │
│  │  BCryptPasswordEncoder passwordEncoder()               │     │
│  │    - Provides BCrypt encoder for password hashing      │     │
│  │    - Used by UserService                               │     │
│  └────────────────────────────────────────────────────────┘     │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
                         ↑ / ↓ (MongoDB Protocol)
┌─────────────────────────────────────────────────────────────────┐
│                      DATABASE (MongoDB)                          │
│                                                                   │
│  Collection: personality-analyzer.users                          │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ Document Example:                                      │     │
│  │ {                                                      │     │
│  │   "_id": ObjectId("..."),                              │     │
│  │   "fullName": "John Doe",                              │     │
│  │   "email": "john@example.com",                         │     │
│  │   "password": "$2a$10$...",        // BCrypt hash      │     │
│  │   "personalityType": "UNANALYZED",                     │     │
│  │   "averageDup": 0.0,                                   │     │
│  │   "completionRate": 0,                                 │     │
│  │   "createdAt": 1704067200000,                          │     │
│  │   "updatedAt": 1704067200000                           │     │
│  │ }                                                      │     │
│  └────────────────────────────────────────────────────────┘     │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Registration Flow

```
User                    Browser                  Backend              MongoDB
│                          │                         │                   │
│─── Fill Form ──────────→ │                         │                   │
│                          │                         │                   │
│                          │─ POST /api/auth/register─→ AuthController   │
│                          │   {fullName, email,     │                   │
│                          │    password,            │                   │
│                          │    confirmPassword}     │                   │
│                          │                         │                   │
│                          │                    ↓ UserService            │
│                          │                    • Validate inputs         │
│                          │                    • Check duplicate email   │
│                          │                    • Hash password (BCrypt)  │
│                          │                    • Create User object     │
│                          │                         │                   │
│                          │                         │── save(user) ─────→
│                          │                         │                   │
│                          │                         │←─ User saved ─────│
│                          │                         │                   │
│                          │←─ AuthResponse ────────│                   │
│                          │   {success: true,      │                   │
│                          │    userId, ...}        │                   │
│                          │                        │                   │
│← Success Message ────────│                        │                   │
│                          │                        │                   │
│                          │ Store in localStorage  │                   │
│                          │ (userId, authToken)    │                   │
│                          │                        │                   │
│← Redirect to Dashboard ──│                        │                   │
│                          │                        │                   │
```

---

## 3. Login Flow

```
User                    Browser                  Backend              MongoDB
│                          │                         │                   │
│─── Fill Form ──────────→ │                         │                   │
│                          │                         │                   │
│                          │─ POST /api/auth/login ─→ AuthController    │
│                          │   {email, password}    │                   │
│                          │                         │                   │
│                          │                    ↓ UserService            │
│                          │                    • Find user by email      │
│                          │                         │                   │
│                          │                         │─ findByEmail ─────→
│                          │                         │                   │
│                          │                         │←─ User found ─────│
│                          │                         │                   │
│                          │                    • Verify password        │
│                          │                      (BCrypt.matches)       │
│                          │                    • Update timestamp       │
│                          │                    • Create AuthResponse    │
│                          │                         │                   │
│                          │←─ AuthResponse ────────│                   │
│                          │   {success: true,      │                   │
│                          │    userId, ...}        │                   │
│                          │                        │                   │
│← Success Message ────────│                        │                   │
│                          │                        │                   │
│                          │ Store in localStorage  │                   │
│                          │ (userId, authToken)    │                   │
│                          │                        │                   │
│← Redirect to Dashboard ──│                        │                   │
│                          │                        │                   │
```

---

## 4. Protected Page Access Flow

```
User                    Browser                  Page Script
│                          │                         │
│─ Navigate to ───────────→│ dashboard.html         │
│   /dashboard             │                         │
│                          │─ Load page ────────────→│
│                          │                         │
│                          │        ↓ auth-utils.js │
│                          │        requireLogin()  │
│                          │         │              │
│                          │    isUserLoggedIn()?   │
│                          │         │              │
│                      NO  │←────────┴──────→ Check localStorage
│                          │                  (userId, authToken)
│                          │                         │
│─ Redirect to /login ────│←─ Redirect ─────────────│
│                          │                         │
│                    OR                              │
│                                                    │
│                      YES                           │
│                          │                         │
│                          │ Display user info ←────│
│                          │ Show "Welcome, John!"  │
│                          │ Setup logout button    │
│                          │                        │
│← Dashboard displays ──────│                        │
│                          │                        │
```

---

## 5. Password Hashing Flow

```
User Input Password
│
↓
┌─────────────────────────────┐
│ UserService.registerUser()  │
├─────────────────────────────┤
│                             │
│ plainPassword = request     │
│   .getPassword()            │
│   = "MyPassword123"         │
│                             │
└─────────────────────────────┘
│
↓
┌─────────────────────────────────────────┐
│ BCryptPasswordEncoder                   │
│   .encode(plainPassword)                │
├─────────────────────────────────────────┤
│                                         │
│ Step 1: Generate random salt            │
│    salt = random 16 bytes               │
│                                         │
│ Step 2: Hash password with salt         │
│    hash = BCRYPT(password, salt)        │
│                                         │
│ Step 3: Return encoded string           │
│    encoded = "$2a$10$..." (60 chars)    │
│                                         │
│ Each call produces DIFFERENT hash       │
│ even with same password (due to salt)   │
│                                         │
└─────────────────────────────────────────┘
│
↓
┌─────────────────────────────┐
│ Save to MongoDB             │
├─────────────────────────────┤
│ {                           │
│   fullName: "John Doe",     │
│   email: "john@...",        │
│   password: "$2a$10$..."    │ ← Hashed, NOT plain text
│ }                           │
└─────────────────────────────┘
│
↓
On Login:
│
BCryptPasswordEncoder
  .matches(inputPassword, hashedPassword)
  │
  ├─ Input: "MyPassword123"
  ├─ Stored: "$2a$10$..."
  │
  └─ Returns: true if match, false otherwise
```

---

## 6. Error Handling Flow

```
User Request
│
↓
AuthController validates input
│
├─→ Email empty? ──→ 400 Bad Request
│                   "Email is required"
│
├─→ Password empty? ──→ 400 Bad Request
│                      "Password is required"
│
├─→ For Registration:
│   ├─→ Passwords don't match?
│   │   AuthResponse {
│   │     success: false,
│   │     message: "Passwords do not match"
│   │   }
│   │
│   ├─→ Email already exists?
│   │   AuthResponse {
│   │     success: false,
│   │     message: "Email already registered"
│   │   }
│   │
│   ├─→ Invalid email format?
│   │   AuthResponse {
│   │     success: false,
│   │     message: "Invalid email format"
│   │   }
│   │
│   ├─→ Password too short?
│   │   AuthResponse {
│   │     success: false,
│   │     message: "Password must be at least 6 characters"
│   │   }
│   │
│   └─→ All valid?
│       AuthResponse {
│         success: true,
│         message: "User registered successfully",
│         userId, fullName, email
│       }
│
├─→ For Login:
│   ├─→ User not found OR password wrong?
│   │   AuthResponse {
│   │     success: false,
│   │     message: "Invalid email or password"
│   │   }
│   │   ↑ Generic message for security
│   │
│   └─→ Credentials valid?
│       AuthResponse {
│         success: true,
│         message: "Login successful",
│         userId, fullName, email
│       }
│
↓
Response sent to frontend
│
Frontend checks response.success
│
├─ true → Store data, redirect to dashboard
└─ false → Display error message
```

---

## 7. Request/Response Example

### Registration Request

```
POST /api/auth/register HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "confirmPassword": "SecurePass123"
}
```

### Registration Response (Success - 201)

```
HTTP/1.1 201 Created
Content-Type: application/json

{
  "success": true,
  "message": "User registered successfully",
  "userId": "507f1f77bcf86cd799439011",
  "fullName": "John Doe",
  "email": "john@example.com"
}
```

### Registration Response (Error - 400)

```
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "success": false,
  "message": "Email already registered",
  "userId": null,
  "fullName": null,
  "email": null
}
```

### Login Request

```
POST /api/auth/login HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

### Login Response (Success - 200)

```
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Login successful",
  "userId": "507f1f77bcf86cd799439011",
  "fullName": "John Doe",
  "email": "john@example.com"
}
```

### Login Response (Error - 401)

```
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "success": false,
  "message": "Invalid email or password",
  "userId": null,
  "fullName": null,
  "email": null
}
```

---

## 8. Component Interaction

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Entry                        │
│            DeadlineAnalyzerApplication.java                 │
│                (Starts on port 8080)                        │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┬──────────────┐
        │              │              │              │
        ↓              ↓              ↓              ↓
   ┌────────┐  ┌────────────┐  ┌──────────┐  ┌──────────────┐
   │ Servlet│  │ Data Model │  │ Service  │  │ Controller   │
   │Handler │  │            │  │  Layer   │  │   Layer      │
   └────────┘  └────────────┘  └──────────┘  └──────────────┘
        │              │              │              │
        │              │              │              │
        │         ┌────↓─────┐  ┌────↓─────┐        │
        │         │           │  │           │        │
        │    ┌────►   User    ├─►UserService├──┬────►│
        │    │    │           │  │           │  │     │
        │    │    └───────────┘  └───────────┘  │     │
        │    │                                  │     │
        │    │  ┌────────────────┐              │     │
        │    │  │ UserRepository │◄─────────────┘     │
        │    │  └────────────────┘                    │
        │    │         │                              │
        │    │         ↓                              │
        │    │    ┌─────────────┐                     │
        │    │    │  MongoDB    │                     │
        │    │    │  Database   │                     │
        │    │    └─────────────┘                     │
        │    │                                        │
        │    └────────────────────────────────────────┘
        │                                             │
        └─────────────────────────────────────────────┘
                        ↓
                  REST API Response
                   (JSON)
```

---

## 9. File Dependencies

```
Frontend Files
│
├─ login.html
│  ├─ imports: auth.js
│  │           └─ contains: Form submission handlers
│  │                       API calls to backend
│  │
│  └─ form elements: fullName, email, password, etc.
│

Protected Pages (dashboard.html, tasks.html)
│
├─ imports: auth-utils.js
│  │
│  ├─ contains: initProtectedPage()
│  │           logout()
│  │           getCurrentUser()
│  │
│  └─ called: At page load
│            Checks if user logged in
│            Protects page access
│


Backend Files
│
├─ DeadlineAnalyzerApplication.java
│  └─ starts Spring Boot application
│
├─ AuthController.java
│  ├─ @PostMapping("/api/auth/register")
│  ├─ @PostMapping("/api/auth/login")
│  └─ calls: UserService methods
│
├─ UserService.java
│  ├─ registerUser(RegisterRequest)
│  ├─ loginUser(LoginRequest)
│  └─ uses: UserRepository, BCryptPasswordEncoder
│
├─ UserRepository.java
│  ├─ extends: MongoRepository
│  └─ methods: findByEmail, save, findById
│
├─ User.java
│  └─ @Document(collection = "users")
│
├─ SecurityConfig.java
│  └─ @Bean BCryptPasswordEncoder
│
└─ DTOs (LoginRequest, RegisterRequest, AuthResponse)
   └─ data carriers for requests/responses
```

---

## 10. Data Flow Timeline

```
Timeline: User Registration & Login Process

T1: User arrives at http://localhost:8080/login
    │
    ├─ Browser loads login.html
    ├─ Loads auth.js
    ├─ Form handlers attached
    │
    └─ Ready for user input

T2: User clicks "Sign Up" tab
    │
    ├─ JavaScript toggles visibility
    ├─ Signup form shown
    │
    └─ User enters: fullName, email, password, confirmPassword

T3: User clicks "Create Account"
    │
    ├─ auth.js collects form data
    ├─ Validates locally (passwords match, required fields)
    ├─ Creates JSON request body
    ├─ Button shows "Creating account..." state
    │
    └─ Sends: POST /api/auth/register

T4: Backend receives registration request
    │
    ├─ AuthController.register() processes request
    ├─ Calls UserService.registerUser(RegisterRequest)
    │  ├─ Validates passwords match
    │  ├─ Checks for duplicate email
    │  ├─ Validates email format
    │  ├─ Checks password strength
    │  ├─ Hashes password with BCrypt
    │  ├─ Calls userRepository.save(User)
    │  │  └─ MongoDB saves document
    │  └─ Returns AuthResponse
    │
    └─ Returns: 201 Created with AuthResponse

T5: Frontend receives success response
    │
    ├─ JavaScript detects response.success = true
    ├─ Stores in localStorage:
    │  ├─ userId
    │  ├─ userFullName
    │  ├─ userEmail
    │  ├─ authToken
    │
    ├─ Shows success message
    ├─ Waits 1 second
    │
    └─ window.location.href = 'dashboard.html'

T6: Browser navigates to dashboard.html
    │
    ├─ Loads auth-utils.js
    ├─ Script runs: initProtectedPage()
    │  ├─ Calls requireLogin()
    │  ├─ Checks localStorage for userId & authToken
    │  ├─ User is logged in ✓
    │  ├─ Displays user name from localStorage
    │  ├─ Attaches logout handler
    │
    └─ Dashboard fully loaded and functional

T7: User later closes browser, comes back
    │
    ├─ Opens http://localhost:8080/dashboard
    ├─ auth-utils.js runs requireLogin()
    ├─ Checks localStorage
    ├─ localStorage still has user data (persistent)
    │
    └─ Dashboard loads without login

T8: User clicks "Logout"
    │
    ├─ Button calls logout() function
    ├─ Clears all localStorage keys
    ├─ Redirects to /login
    │
    └─ User logged out

T9: User tries to access dashboard
    │
    ├─ Opens http://localhost:8080/dashboard
    ├─ auth-utils.js runs requireLogin()
    ├─ Checks localStorage
    ├─ localStorage is empty
    ├─ Calls window.location.href = '/login'
    │
    └─ Redirected to login page

T10: User re-enters credentials for login
    │
    ├─ Fills email and password
    ├─ Clicks "Sign In"
    ├─ auth.js validates and sends POST /api/auth/login
    ├─ Backend verifies credentials with BCrypt
    ├─ Returns user data
    ├─ Frontend stores in localStorage
    ├─ Redirects to dashboard
    │
    └─ User logged back in
```

This visual flow shows the entire authentication lifecycle!

---

## Quick Reference

### Files to Modify (When Extending)

| Task | File | Method |
|------|------|--------|
| Add new user field | User.java | Add field + getter/setter |
| Change password rules | UserService.java | Modify validation in registerUser() |
| Add new API endpoint | AuthController.java | Add new @PostMapping method |
| Protect new page | page.html | Import auth-utils.js, call initProtectedPage() |
| Change redirect URL | auth.js | Modify window.location.href |

### Key Classes at a Glance

| Class | Package | Responsibility |
|-------|---------|-----------------|
| AuthController | controller | REST endpoints |
| UserService | service | Business logic |
| UserRepository | repository | Database access |
| User | model | Data structure |
| SecurityConfig | config | Bean configuration |
| LoginRequest | dto | Login request DTO |
| RegisterRequest | dto | Register request DTO |
| AuthResponse | dto | API response DTO |

---

This visual guide helps understand how all components work together!

