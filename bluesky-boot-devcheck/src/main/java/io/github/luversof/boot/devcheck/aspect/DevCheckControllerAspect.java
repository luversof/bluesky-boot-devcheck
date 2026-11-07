package io.github.luversof.boot.devcheck.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import io.github.luversof.boot.devcheck.DevCheckProperties;
import io.github.luversof.boot.devcheck.exception.DevCheckException;

@Aspect
public class DevCheckControllerAspect {

  private final DevCheckProperties devCheckProperties;

  public DevCheckControllerAspect(DevCheckProperties devCheckProperties) {
    this.devCheckProperties = devCheckProperties;
  }

  @Around("@within(io.github.luversof.boot.devcheck.annotation.DevCheckController)")
  public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    if (!devCheckProperties.isEnabled()) {
      throw new DevCheckException();
    }
    return proceedingJoinPoint.proceed();
  }
}
