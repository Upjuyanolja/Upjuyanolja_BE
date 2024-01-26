package com.backoffice.upjuyanolja.global.concurrency.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP에서 트랜잭션 분리를 위한 클래스
 */
@Component
public class TransactionAspect {

    // leaseTime 보다 트랜잭션 타임아웃을 작게 설정
    // leaseTimeOut 발생 전에 rollback 시키기 위함
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 9)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
