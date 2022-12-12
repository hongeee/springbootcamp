package com.vroong.order.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.Arrays;

@Aspect
@Slf4j
public class LoggingAspect {

  private final Environment env;

  public LoggingAspect(Environment env) {
    this.env = env;
  }

  /** Pointcut that matches all repositories, services and Web REST endpoints. */
  @Pointcut(
      "within(@org.springframework.stereotype.Repository *)"
          + " || within(@org.springframework.stereotype.Service *)"
          + " || within(@org.springframework.web.bind.annotation.RestController *)")
  public void springBeanPointcut() {
    // Method is empty as this is just a Pointcut, the implementations are in the advices.
  }

  /** Pointcut that matches all Spring beans in the application's main packages. */
  @Pointcut("within(com.vroong.order..*)")
  public void applicationPackagePointcut() {
    // Method is empty as this is just a Pointcut, the implementations are in the advices.
  }

  /**
   * Advice that logs methods throwing exceptions.
   *
   * @param joinPoint join point for advice.
   * @param e exception.
   */
  @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
    if (env.acceptsProfiles(Profiles.of("dev"))) {
      log.error(
          "Exception in {}.{}() with cause = \'{}\' and exception = \'{}\'",
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName(),
          e.getCause() != null ? e.getCause() : "NULL",
          e.getMessage(),
          e);
    } else {
      log.error(
          "Exception in {}.{}() with cause = {}",
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName(),
          e.getCause() != null ? e.getCause() : "NULL");
    }
  }

  /**
   * Advice that logs when a method is entered and exited.
   *
   * @param joinPoint join point for advice.
   * @return result.
   * @throws Throwable throws {@link IllegalArgumentException}.
   */
  @Around("applicationPackagePointcut() && springBeanPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    if (log.isDebugEnabled()) {
      log.debug(
          "Enter: {}.{}() with argument[s] = {}",
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName(),
          Arrays.toString(joinPoint.getArgs()));
    }

    try {
      Object result = joinPoint.proceed();
      if (log.isDebugEnabled()) {
        log.debug(
            "Exit: {}.{}() with result = {}",
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            result);
      }

      return result;
    } catch (IllegalArgumentException e) {
      log.error(
          "Illegal argument: {} in {}.{}()",
          Arrays.toString(joinPoint.getArgs()),
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName());

      throw e;
    }
  }
}