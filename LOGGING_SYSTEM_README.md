# Performance and Activity Logging System

This document explains the complete logging and performance tracking system implemented in the Personality Analyzer project.

## 📁 File Structure

```
src/main/java/com/deadline/analyzer/
├── model/
│   └── ActivityLog.java              # Database model for activity logs
├── repository/
│   └── ActivityLogRepository.java    # MongoDB repository for logs
├── service/
│   └── ActivityLogService.java       # Service for logging operations
├── aspect/
│   ├── PerformanceTrackingAspect.java # AOP aspect for performance tracking
│   └── TrackPerformance.java         # Annotation for marking methods
├── dto/
│   └── PerformanceData.java          # DTO for performance data
├── controller/
│   ├── AuthController.java           # Updated with performance tracking
│   └── TestController.java           # Example controller
└── resources/
    └── logback-spring.xml            # Logging configuration

src/main/resources/static/js/
└── performance-tracker.js            # Frontend performance tracking
```

## 🚀 How It Works

### 1. Backend Performance Tracking

**Automatic Tracking with AOP:**
- Add `@TrackPerformance(action = "Description")` annotation to any method
- The aspect automatically tracks execution time
- Logs are saved to both database and files

**Example:**
```java
@PostMapping("/register")
@TrackPerformance(action = "User Registration")
public ResponseEntity<Map<String, Object>> register(...) {
    // Method implementation
}
```

### 2. Frontend User Input Tracking

**Automatic Form Tracking:**
- Tracks time from first input focus to form submission
- Works automatically for login and registration forms
- Sends data to `/api/auth/performance` endpoint

**Manual Tracking:**
```javascript
// Start tracking
const tracker = trackPerformance('Custom Action', 'user@example.com');

// End tracking
tracker.end();
```

### 3. Logging Destinations

**File Logging:**
- `logs/activity.log` - General activity logs
- `logs/performance.log` - Performance metrics
- Automatic rotation (10MB files, 30 days retention)

**Database Logging:**
- MongoDB `activity_logs` collection
- Includes user info, timestamps, IP addresses, performance data

## 📊 What Gets Tracked

### User Actions:
- User registration
- User login
- Form submissions
- Custom actions (with annotation)

### Performance Metrics:
- **Input Time:** Time user spends filling forms (frontend)
- **Processing Time:** Backend execution time (AOP)
- **Total Time:** Complete end-to-end duration

### Context Information:
- User email/IP address
- User agent string
- Timestamp (UTC)
- Action details
- Success/failure status

## 🔧 Configuration

### Logback Configuration (`logback-spring.xml`)
- Separate loggers for different log types
- Rolling file policies
- Console and file output

### Dependencies Added:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>
```

## 📝 Log Examples

### Performance Log Entry:
```
2024-04-02 10:30:45.123 [http-nio-8080-exec-1] INFO  c.d.a.service.ActivityLogService - PERFORMANCE_LOG: USER: user@example.com | ACTION: User Registration | DETAILS: AuthController.register completed successfully | INPUT_TIME: 5000ms | PROCESSING_TIME: 150ms | IP: 192.168.1.100 | TIMESTAMP: 1712043045123
```

### Activity Log Entry:
```
2024-04-02 10:30:45.123 [http-nio-8080-exec-1] INFO  c.d.a.service.ActivityLogService - ACTIVITY_LOG: USER: user@example.com | ACTION: User Login | DETAILS: AuthController.login completed successfully | INPUT_TIME: null | PROCESSING_TIME: null | IP: 192.168.1.100 | TIMESTAMP: 1712043045123
```

## 🎯 Usage Examples

### 1. Track a New Controller Method:
```java
@PostMapping("/update-profile")
@TrackPerformance(action = "Profile Update")
public ResponseEntity<?> updateProfile(@RequestBody ProfileData data) {
    // Your implementation
}
```

### 2. Track Custom Frontend Action:
```javascript
// In your JavaScript
const tracker = trackPerformance('Task Creation', 'user@example.com');
// ... do some work ...
tracker.end();
```

### 3. Manual Activity Logging:
```java
@Autowired
private ActivityLogService activityLogService;

// Log any activity
activityLogService.logActivity(
    "user@example.com",
    "Custom Action",
    "User performed some custom operation"
);
```

## 🔍 Viewing Logs

### Console Output:
- Logs appear in console during development
- Different levels for different log types

### File Logs:
- `logs/activity.log` - All user activities
- `logs/performance.log` - Performance metrics only

### Database Logs:
```javascript
// Query MongoDB
db.activity_logs.find().sort({timestamp: -1}).limit(10)
```

## 🚨 Error Handling

### Frontend Errors:
- Failed network requests are logged to console
- Performance tracking continues even if logging fails

### Backend Errors:
- Database logging failures don't break the application
- Errors are logged to console as fallback
- File logging continues even if database fails

## 📈 Performance Impact

### Minimal Overhead:
- AOP aspect adds ~1-2ms to method execution
- File logging is asynchronous
- Database logging uses connection pooling

### Best Practices:
- Use `@TrackPerformance` only on important methods
- Keep log messages concise
- Monitor log file sizes and rotation

## 🔄 Testing the System

### 1. Test Backend Tracking:
```bash
curl -X POST http://localhost:8080/api/test/process-data \
  -H "Content-Type: application/json" \
  -d '{"field1": "value1", "field2": "value2"}'
```

### 2. Test Frontend Tracking:
1. Open login page
2. Start typing in email field
3. Fill form and submit
4. Check logs for timing data

### 3. Test Performance Endpoint:
```bash
curl -X POST http://localhost:8080/api/auth/performance \
  -H "Content-Type: application/json" \
  -d '{
    "userEmail": "test@example.com",
    "action": "Test Action",
    "inputTimeMs": 5000,
    "details": "Test performance tracking"
  }'
```

## 🎛️ Customization

### Add New Log Types:
1. Update `logback-spring.xml`
2. Add new appenders as needed
3. Update `ActivityLogService.logToFile()` method

### Custom Performance Metrics:
1. Extend `PerformanceData` DTO
2. Update frontend tracking script
3. Modify database schema if needed

### Different Database:
1. Change `ActivityLog` entity annotations
2. Update repository interface
3. Modify database configuration

This comprehensive logging system provides complete visibility into user behavior and system performance while maintaining clean separation of concerns and minimal performance impact.
