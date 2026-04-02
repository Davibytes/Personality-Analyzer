package com.deadline.analyzer.aspect;

import com.deadline.analyzer.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceTrackingAspect {

    private final ActivityLogService activityLogService;

    /**
     * Track performance of methods annotated with @TrackPerformance
     */
    @Around("@annotation(trackPerformance)")
    public Object trackPerformance(ProceedingJoinPoint joinPoint, TrackPerformance trackPerformance) throws Throwable {
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String action = trackPerformance.action();
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Proceed with the method execution
            Object result = joinPoint.proceed();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Log performance
            log.info("PERFORMANCE: {}.{} executed in {}ms", className, methodName, duration);
            
            // If this is a controller method, log user activity
            if (className.contains("Controller")) {
                HttpServletRequest request = getCurrentRequest();
                String userEmail = extractUserEmail(joinPoint);
                
                activityLogService.logActivity(
                    userEmail,
                    action,
                    String.format("%s.%s completed successfully", className, methodName),
                    null,  // inputTimeMs will be set by frontend
                    duration,
                    request
                );
            }
            
            return result;
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.error("PERFORMANCE: {}.{} failed after {}ms - {}", className, methodName, duration, e.getMessage());
            
            // Log failed activity
            HttpServletRequest request = getCurrentRequest();
            String userEmail = extractUserEmail(joinPoint);
            
            activityLogService.logActivity(
                userEmail,
                action,
                String.format("%s.%s failed: %s", className, methodName, e.getMessage()),
                null,
                duration,
                request
            );
            
            throw e;
        }
    }

    /**
     * Extract user email from method parameters (looks for email parameter)
     */
    private String extractUserEmail(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        
        // Look for email in parameters
        for (Object arg : args) {
            if (arg instanceof String) {
                String strArg = (String) arg;
                if (strArg.contains("@")) {
                    return strArg;
                }
            }
        }
        
        return "anonymous";
    }

    /**
     * Get current HTTP request
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }
}
