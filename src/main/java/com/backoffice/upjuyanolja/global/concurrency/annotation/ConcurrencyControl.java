package com.backoffice.upjuyanolja.global.concurrency.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Description;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Description("Concurrency control annotation to acquire a lock on the target resource.")
public @interface ConcurrencyControl {

    /**
     * The name of the target resource to acquire a lock on.
     */
    String lockName();

    /**
     * The maximum time to wait for the lock to be available, in the specified time unit.
     */
    long waitTime() default 10L;

    /**
     * The duration to hold the lock for, in the specified time unit.
     */
    long leaseTime() default 10L;

    /**
     * The time unit for the waitTime and leaseTime values.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
