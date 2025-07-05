package com.jobpulse.auth_service.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Aspect for automatically adding comprehensive metrics to all controller endpoints.
 * This provides consistent tagging and measurement across all REST endpoints without
 * requiring controllers to be aware of metrics collection.
 */
@Aspect
@Component
public class EndpointMetricsAspect {

    private final MeterRegistry meterRegistry;

    public EndpointMetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Intercept all controller methods and add comprehensive metrics with proper tagging.
     */
    @Around("execution(* com.jobpulse.auth_service.controller..*.*(..))")
    public Object measureEndpoint(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            return joinPoint.proceed();
        }

        String method = request.getMethod();
        String uri = normalizeUri(request.getRequestURI());
        String operation = determineOperation(joinPoint.getSignature().getName(), uri);
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Object result = joinPoint.proceed();
            
            // Determine response status
            int statusCode = extractStatusCode(result);
            String statusClass = getStatusClass(statusCode);
            
            // Get user context for business metrics
            String userRole = getUserRole();
            
            // Record metrics with comprehensive tags
            recordMetrics(method, uri, operation, statusClass, userRole, true);
            
            // Record timing
            sample.stop(Timer.builder("http_request_duration")
                    .description("HTTP request duration")
                    .tags(createBaseTags(method, uri, operation, statusClass, userRole))
                    .register(meterRegistry));
            
            return result;
            
        } catch (Exception e) {
            // Record error metrics
            String userRole = getUserRole();
            recordMetrics(method, uri, operation, "5xx", userRole, false);
            
            sample.stop(Timer.builder("http_request_duration")
                    .description("HTTP request duration")
                    .tags(createBaseTags(method, uri, operation, "5xx", userRole)
                            .and("error", e.getClass().getSimpleName()))
                    .register(meterRegistry));
            
            throw e;
        }
    }

    private void recordMetrics(String method, String uri, String operation, String statusClass, String userRole, boolean success) {
        Tags baseTags = createBaseTags(method, uri, operation, statusClass, userRole);
        
        // Request counter
        Counter.builder("http_requests_total")
                .description("Total HTTP requests")
                .tags(baseTags)
                .register(meterRegistry)
                .increment();
        
        // Business operation counter
        Counter.builder("business_operations_total")
                .description("Total business operations by type")
                .tags(baseTags.and("success", String.valueOf(success)))
                .register(meterRegistry)
                .increment();
        
        // Record operation-specific metrics
        recordOperationSpecificMetrics(operation, userRole, success);
    }

    private void recordOperationSpecificMetrics(String operation, String userRole, boolean success) {
        Tags operationTags = Tags.of(
                "service", "auth-service",
                "operation", operation,
                "user_role", userRole,
                "success", String.valueOf(success)
        );
        
        switch (operation) {
            case "user_registration":
                Counter.builder("auth_registration_attempts_total")
                        .description("Total user registration attempts")
                        .tags(operationTags)
                        .register(meterRegistry)
                        .increment();
                break;
                
            case "user_login":
                Counter.builder("auth_login_attempts_total")
                        .description("Total user login attempts")
                        .tags(operationTags)
                        .register(meterRegistry)
                        .increment();
                break;
                
            case "token_validation":
                Counter.builder("auth_token_validations_total")
                        .description("Total token validation attempts")
                        .tags(operationTags)
                        .register(meterRegistry)
                        .increment();
                break;
        }
    }

    private Tags createBaseTags(String method, String uri, String operation, String statusClass, String userRole) {
        return Tags.of(
                "service", "auth-service",
                "method", method,
                "uri", uri,
                "operation", operation,
                "status_class", statusClass,
                "user_role", userRole,
                "environment", System.getProperty("spring.profiles.active", "dev")
        );
    }

    private String determineOperation(String methodName, String uri) {
        // Map controller method names and URIs to business operations
        if (methodName.toLowerCase().contains("register") || uri.contains("/register")) {
            return "user_registration";
        } else if (methodName.toLowerCase().contains("login") || uri.contains("/login")) {
            return "user_login";
        } else if (methodName.toLowerCase().contains("validate") || methodName.toLowerCase().contains("verify")) {
            return "token_validation";
        } else if (uri.contains("/ping")) {
            return "health_check";
        }
        
        // Default fallback
        return methodName.toLowerCase().replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    private String normalizeUri(String uri) {
        // Normalize URIs to remove path parameters for consistent grouping
        return uri.replaceAll("/\\d+", "/{id}")
                  .replaceAll("/[a-f0-9\\-]{36}", "/{uuid}")
                  .replaceAll("\\?.*", ""); // Remove query parameters
    }

    private int extractStatusCode(Object result) {
        if (result instanceof ResponseEntity) {
            return ((ResponseEntity<?>) result).getStatusCode().value();
        }
        return 200; // Default for successful @RestController methods
    }

    private String getStatusClass(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) return "2xx";
        if (statusCode >= 300 && statusCode < 400) return "3xx";
        if (statusCode >= 400 && statusCode < 500) return "4xx";
        if (statusCode >= 500) return "5xx";
        return "unknown";
    }

    private String getUserRole() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) auth.getPrincipal();
                String role = jwt.getClaimAsString("role");
                return role != null ? role : "unknown";
            }
        } catch (Exception e) {
            // Ignore authentication extraction errors
        }
        return "anonymous";
    }

    private HttpServletRequest getCurrentHttpRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }
}
