package com.backoffice.upjuyanolja.global.concurrency.aspect;

import com.backoffice.upjuyanolja.global.concurrency.annotation.ConcurrencyControl;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("prod")
@Aspect
@Component
@RequiredArgsConstructor
public class ConcurrencyAspect {

    private final RedissonClient redissonClient;
    private final TransactionAspect transactionAspect;

    @Around("@annotation(com.backoffice.upjuyanolja.global.concurrency.annotation.ConcurrencyControl)&&args(targetId)")
    public Object handleConcurrency(ProceedingJoinPoint joinPoint, Long targetId) throws Throwable {
        Object result;

        // Get annotation
        ConcurrencyControl annotation = getAnnotation(joinPoint);

        // Get lock name and acquire lock
        String lockName = getLockName(targetId, annotation);
        RLock lock = redissonClient.getLock(lockName);

        try {
            boolean available = lock.tryLock(annotation.waitTime(), annotation.leaseTime(),
                annotation.timeUnit());

            if (!available) {
                log.warn("Redisson GetLock Timeout {}", lockName);
                throw new IllegalArgumentException();
            }

            // Proceed with the original method execution
            return transactionAspect.proceed(joinPoint);
        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.warn("Redisson Lock Already UnLock {}", lockName);
            }
        }
    }

    private ConcurrencyControl getAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(ConcurrencyControl.class);
    }

    private String getLockName(Long targetId, ConcurrencyControl annotation) {
        String lockNameFormat = "lock:%s:%s";
        String relevantParameter = targetId.toString();
        return String.format(lockNameFormat, annotation.lockName(), relevantParameter);
    }
}
