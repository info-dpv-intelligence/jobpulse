package com.jobpulse.auth_service.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark service methods that should automatically publish domain events.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishDomainEvents {
    
    /**
     * Whether to fail the entire transaction if domain event publishing fails.
     */
    boolean failOnError() default true;
    
    /**
     * Whether to publish events asynchronously.
     */
    boolean async() default false;
    
    /**
     * Optional description for documentation purposes.
     */
    String value() default "";
}
