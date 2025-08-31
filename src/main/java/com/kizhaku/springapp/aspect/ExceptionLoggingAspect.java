package com.kizhaku.springapp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class ExceptionLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionLoggingAspect.class);

    //Exclude controller error
    @Pointcut("execution(* com.kizhaku.springapp.controller..*(..))")
    private void excludedPackage() {}

    @Pointcut("execution(* com.kizhaku.springapp..*(..))")
    private void includedPackage() {}

    @AfterThrowing(pointcut = "includedPackage() && !excludedPackage()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        logger.error("An exception was thrown at {} with arguments {} and exception {}",
                joinPoint.getSignature(),
                Arrays.toString(joinPoint.getArgs()),
                ex.getMessage(),
                ex);
    }
}
