package io.github.luversof.boot.devcheck.annotation;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@code bluesky-boot.dev-check.enabled=true} 일 때만 통과한다. 앱이 직접 만든 {@link DevCheckController} 도 꺼진
 * 상태에서는 등록되지 않게 한다 - 자동 설정만 꺼지면 앱의 컴포넌트 스캔으로 접두어 없이 열린다.
 */
public class DevCheckEnabledCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    return context
        .getEnvironment()
        .getProperty("bluesky-boot.dev-check.enabled", Boolean.class, false);
  }
}
