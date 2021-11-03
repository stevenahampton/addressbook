package com.stevenhampton.addressbook.logging;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Log details for all service methods.
 */
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServiceLoggingAspect {
    private final @NonNull LoggingHelper loggingHelper;

    @Pointcut("execution(* com.stevenhampton.addressbook.service.*.*(..))")
    public void methodsToLog() {
        // no body required
    }

    @Around("methodsToLog()")
    public Object logMethodCallWithParameters(ProceedingJoinPoint joinPoint) throws Throwable {
        return loggingHelper.logMethodCall(log, joinPoint);
    }
}
