# Authentication Testing Guide

## 🧪 Complete Testing Checklist

Follow these steps to verify that the authentication system works correctly.

---

## 1. Environment Setup

### Prerequisites Check
- [ ] Java 17+ installed: `java -version`
- [ ] Maven available (IntelliJ will handle this)
- [ ] MongoDB running
- [ ] IntelliJ IDEA open with project loaded

### MongoDB Verification
```powershell
# Check if MongoDB is running
Get-Process mongod

# Or start it
mongod --dbpath "C:\Program Files\MongoDB\Server\data\db"

# Connect to verify
# Use MongoDB Compass or mongosh CLI
```

---

## 2. Build and Start the Application

### Step 1: Reload Maven Dependencies
1. Open `pom.xml`
2. Right-click project root
3. Select **Maven > Reload Project**
4. Wait for download to complete (observe progress in bottom right)
5. Verify no dependency errors

### Step 2: Run the Application
1. Open `src/main/java/com/deadline/analyzer/DeadlineAnalyzerApplication.java`
2. Right-click the file
3. Select **Run 'DeadlineAnalyzerApplication.main()'**
4. Wait for startup message: **"Tomcat started on port(s): 8080 (http)"**
5. Verify no errors in console

**Expected Console Output:**
```
...
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
o.deadline.analyzer.DeadlineAnalyzerApplication : Started DeadlineAnalyzerApplication in X seconds
```

---

## 3. Test Registration

### 3.1 UI Test - Registration Form

1. Open browser: **http://localhost:8080/login**
2. Verify login form is displayed
3. Click **"Sign Up"** link
4. Verify signup form appears

### 3.2 Test: Valid Registration

**Test Case 1: Complete valid registration**

1. Fill form:
   - Full Name: `John Doe`
   - Email: `john@example.com`
   - Password: `JohnPassword123`
   - Confirm Password: `JohnPassword123`
   - Check: "I agree to Terms of Service"
2. Click **"Create Account"**
3. Expected: 
   - Success message appears
   - Redirects to dashboard after 1 second
   - User data stored in localStorage

**Verify in Browser DevTools (F12):**
- Open **Application > LocalStorage > http://localhost:8080**
- Check for keys:
  - `userId`: Should have MongoDB ID
  - `userFullName`: Should be "John Doe"
  - `userEmail`: Should be "john@example.com"
  - `authToken`: Should start with "bearer_"

### 3.3 Test: Validation Errors

**Test Case 2: Empty fields**
1. Click "Sign Up"
2. Leave all fields empty
3. Click "Create Account"
4. Expected: Error message "Please fill in all fields"

**Test Case 3: Passwords don't match**
1. Fill form with:
   - Password: `Password123`
   - Confirm Password: `Password456`
2. Click "Create Account"
3. Expected: Error message "Passwords do not match"

**Test Case 4: Password too short**
1. Fill form with:
   - Password: `Pass1`
   - Confirm Password: `Pass1`
2. Click "Create Account"
3. Expected: Error message "Password must be at least 6 characters"

**Test Case 5: Did not agree to terms**
1. Fill all fields correctly
2. Leave checkbox unchecked
3. Click "Create Account"
4. Expected: Error message "Please agree to Terms of Service"

**Test Case 6: Duplicate email**
1. Use same email from Test Case 1: `john@example.com`
2. Fill other fields with different data
3. Click "Create Account"
4. Expected: Error message "Email already registered"

**Test Case 7: Invalid email format**
1. Fill form with:
   - Email: `notanemail`
2. Click "Create Account"
3. Expected: May accept or show validation (depending on HTML5 validation)
4. Note: Backend validates email format

---

## 4. Test Login

### 4.1 UI Test - Login Form

1. Go back to **http://localhost:8080/login**
2. Verify login form is displayed
3. Click **"Sign In"** link (if on signup form)
4. Verify login form appears

### 4.2 Test: Valid Login

**Test Case 8: Login with registered credentials**

1. Clear localStorage (F12 > Application > LocalStorage > Clear)
2. Fill login form:
   - Email: `john@example.com`
   - Password: `JohnPassword123`
3. Click **"Sign In"**
4. Expected:
   - Success message appears
   - Redirects to dashboard after 1 second
   - localStorage has user data

**Verify:**
- Open DevTools > Application > LocalStorage
- Check same keys are present as registration

### 4.3 Test: Login Validation Errors

**Test Case 9: Empty credentials**
1. Leave fields empty
2. Click "Sign In"
3. Expected: Error message "Please fill in all fields"

**Test Case 10: Invalid email**
1. Fill form with:
   - Email: `nonexistent@example.com`
   - Password: `JohnPassword123`
2. Click "Sign In"
3. Expected: Error message "Invalid email or password"

**Test Case 11: Wrong password**
1. Fill form with:
   - Email: `john@example.com`
   - Password: `WrongPassword123`
2. Click "Sign In"
3. Expected: Error message "Invalid email or password"

**Test Case 12: Remember Me functionality**
1. Fill login form correctly
2. Check "Remember me" checkbox
3. Click "Sign In"
4. Check localStorage for `rememberEmail` key
5. Expected: Email is stored for next visit

---

## 5. Test Protected Pages

### 5.1 Dashboard Page Protection

**Test Case 13: Access dashboard while logged in**
1. From previous login test, you're on dashboard
2. Refresh page with **F5**
3. Expected: Dashboard loads (user still logged in via localStorage)

**Test Case 14: Access dashboard without login**
1. Clear localStorage completely (F12 > Clear)
2. Go to **http://localhost:8080/dashboard**
3. Expected: Redirects to **http://localhost:8080/login**

**Test Case 15: Display user information**
1. Log in with valid credentials
2. Dashboard should display user's full name and email (if HTML has corresponding elements)
3. Expected: Greeting shows "Welcome, John Doe!"

### 5.2 Tasks Page Protection

**Test Case 16: Access tasks while logged in**
1. Log in
2. Click **"Tasks"** link
3. Expected: Tasks page loads

**Test Case 17: Access tasks without login**
1. Clear localStorage
2. Go to **http://localhost:8080/tasks**
3. Expected: Redirects to login

---

## 6. Test Logout

### 6.1 Logout Functionality

**Test Case 18: Logout from dashboard**
1. Log in
2. On dashboard, click **"Logout"** button
3. Expected:
   - Redirects to login page
   - localStorage is cleared
   - All user data removed

**Verify in DevTools:**
- Open Application > LocalStorage
- All keys (userId, authToken, etc.) should be gone

**Test Case 19: Cannot access protected pages after logout**
1. After logout, try to go to **http://localhost:8080/dashboard**
2. Expected: Redirects back to login

---

## 7. API Testing (Advanced)

### 7.1 Test with Postman or curl

**Test Case 20: API - Register via POST**

```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName": "Jane Smith",
  "email": "jane@example.com",
  "password": "JanePassword123",
  "confirmPassword": "JanePassword123"
}
```

Expected Response (201):
```json
{
  "success": true,
  "message": "User registered successfully",
  "userId": "507f1f77bcf86cd799439011",
  "fullName": "Jane Smith",
  "email": "jane@example.com"
}
```

**Test Case 21: API - Login via POST**

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "jane@example.com",
  "password": "JanePassword123"
}
```

Expected Response (200):
```json
{
  "success": true,
  "message": "Login successful",
  "userId": "507f1f77bcf86cd799439011",
  "fullName": "Jane Smith",
  "email": "jane@example.com"
}
```

---

## 8. Database Verification

### 8.1 Check MongoDB

**Using mongosh CLI:**
```javascript
use personality-analyzer
db.users.find()

// Should return users created during tests:
{
  _id: ObjectId(...),
  fullName: "John Doe",
  email: "john@example.com",
  password: "$2a$10$...",  // BCrypt hashed
  personalityType: "UNANALYZED",
  averageDup: 0,
  completionRate: 0,
  createdAt: 1704067200000,
  updatedAt: 1704067200000
}
```

**Using MongoDB Compass:**
1. Connect to `mongodb://localhost:27017`
2. Navigate to `personality-analyzer > users`
3. Verify documents exist
4. Password should be BCrypt hash (starts with `$2a$`)

### 8.2 Verify Password Hashing

**Important:** Passwords should NEVER be plain text in database.

1. Open any user document in MongoDB
2. Check `password` field
3. Expected format: `$2a$10$NtqQGecceNF3BL4G2FKIP...` (BCrypt hash)
4. Should NOT be: `JohnPassword123` (plain text)

---

## 9. Security Tests

### 9.1 Password Hashing

**Test Case 22: Verify password is hashed**
1. Register user
2. Check database
3. Verify password is NOT stored as plain text
4. Expected: BCrypt hash starting with `$2a$`

### 9.2 Invalid Credentials

**Test Case 23: Generic error message prevents email enumeration**
1. Login with non-existent email
2. Check error message
3. Expected: "Invalid email or password" (not "Email not found")

---

## 10. Browser Compatibility

### Test on Multiple Browsers

- [ ] Chrome/Chromium
- [ ] Firefox
- [ ] Edge
- [ ] Safari (if on Mac)

**For each browser:**
1. Clear cache and cookies
2. Test registration
3. Test login
4. Test logout
5. Verify localStorage works

---

## 11. Error Scenarios

### 11.1 MongoDB Down

**Test Case 24: Handle MongoDB connection failure**

1. Stop MongoDB service
2. Try to register or login
3. Expected: Server error message or timeout
4. Check console for MongoDB connection error

**Fix:** Restart MongoDB and try again

### 11.2 Port Already in Use

**Test Case 25: Handle port 8080 in use**

1. If Tomcat won't start due to port conflict
2. Edit `application.properties`: `server.port=9090`
3. Access at `http://localhost:9090/login`
4. Expected: Works on new port

---

## 12. Performance Tests

### 12.1 Registration Performance

**Test Case 26: Registration completes in reasonable time**
1. Register new user
2. Measure time until redirect
3. Expected: < 2 seconds

### 12.2 Login Performance

**Test Case 27: Login completes quickly**
1. Login with credentials
2. Measure time until redirect
3. Expected: < 1 second

---

## 📋 Test Summary Checklist

### Registration
- [ ] Valid registration succeeds
- [ ] Passwords must match
- [ ] Password minimum length enforced
- [ ] Duplicate email prevented
- [ ] User data stored in localStorage
- [ ] Redirects to dashboard
- [ ] Error messages display correctly

### Login
- [ ] Valid login succeeds
- [ ] Invalid email rejected
- [ ] Invalid password rejected
- [ ] Generic error message for security
- [ ] User data stored in localStorage
- [ ] Redirects to dashboard
- [ ] Remember me functionality works

### Protected Pages
- [ ] Cannot access without login
- [ ] Can access with valid login
- [ ] User info displays correctly
- [ ] Redirects to login when logged out

### Logout
- [ ] Logout clears localStorage
- [ ] Redirects to login page
- [ ] Cannot access protected pages after logout

### API
- [ ] POST /api/auth/register works
- [ ] POST /api/auth/login works
- [ ] Correct HTTP status codes returned
- [ ] Proper error responses

### Database
- [ ] Users stored in MongoDB
- [ ] Passwords are hashed (BCrypt)
- [ ] User fields correct

### Security
- [ ] Passwords not stored in plain text
- [ ] Email validation working
- [ ] CORS enabled for API calls
- [ ] Error messages don't leak information

---

## 🐛 Troubleshooting During Testing

If tests fail, check:

1. **Is MongoDB running?**
   ```powershell
   Get-Process mongod
   ```

2. **Is server running on correct port?**
   - Check console output
   - Try `http://localhost:8080` (or configured port)

3. **Are there console errors?**
   - Open browser DevTools (F12)
   - Check Console tab for JavaScript errors
   - Check Network tab for API call failures

4. **Did you clear localStorage?**
   - Sometimes old data causes issues
   - F12 > Application > LocalStorage > Right-click > Clear

5. **Is the code compiled?**
   - Refresh IntelliJ (Ctrl+Shift+F9)
   - Or redeploy application

---

## ✅ When All Tests Pass

You have successfully implemented a secure, fully-functional authentication system with:

✅ User registration
✅ User login
✅ Password security (BCrypt hashing)
✅ Protected pages
✅ Logout functionality
✅ Data persistence (MongoDB)
✅ Error handling
✅ Input validation

**Congratulations!** The authentication system is ready for production.

---

## 📝 Test Report Template

```
Test Date: ___________
Tester: ___________
Build Version: ___________

RESULTS:
- Registration Tests: [ ] PASS [ ] FAIL
- Login Tests: [ ] PASS [ ] FAIL
- Protected Pages: [ ] PASS [ ] FAIL
- Logout Tests: [ ] PASS [ ] FAIL
- API Tests: [ ] PASS [ ] FAIL
- Database Tests: [ ] PASS [ ] FAIL
- Security Tests: [ ] PASS [ ] FAIL

ISSUES FOUND:
1. 
2. 
3. 

NOTES:

Signed: ___________
```

