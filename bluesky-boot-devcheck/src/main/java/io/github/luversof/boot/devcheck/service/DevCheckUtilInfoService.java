package io.github.luversof.boot.devcheck.service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import io.github.luversof.boot.devcheck.DevCheckProperties;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import io.github.luversof.boot.devcheck.annotation.DevCheckUtil;
import io.github.luversof.boot.devcheck.domain.DevCheckUtilInfo;
import io.github.luversof.boot.devcheck.domain.DevCheckUtilMethodInfo;

public class DevCheckUtilInfoService {

  private static final Logger log = LoggerFactory.getLogger(DevCheckUtilInfoService.class);

  private final DevCheckProperties devCheckProperties;

  /** 한 번 찾은 목록. 동시에 처음 불려도 한 번만 찾는다(예전에는 여러 요청이 같은 목록에 겹쳐 넣었다). */
  private volatile List<DevCheckUtilInfo> devCheckUtilInfoList;

  public DevCheckUtilInfoService(DevCheckProperties devCheckProperties) {
    this.devCheckProperties = devCheckProperties;
  }

  public List<DevCheckUtilInfo> getDevCheckUtilInfo() {
    var cached = devCheckUtilInfoList;
    if (cached != null) {
      return cached;
    }
    synchronized (this) {
      if (devCheckUtilInfoList == null) {
        devCheckUtilInfoList =
            getTargetClassSet().stream().map(this::createDevCheckUtilInfo).toList();
      }
      return devCheckUtilInfoList;
    }
  }

  private Set<Class<?>> getTargetClassSet() {

    ClassPathScanningCandidateComponentProvider provider =
        new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AnnotationTypeFilter(DevCheckUtil.class));

    var beanDefinitionSet = new HashSet<BeanDefinition>();
    devCheckProperties
        .getBasePackages()
        .forEach(
            basePackage -> beanDefinitionSet.addAll(provider.findCandidateComponents(basePackage)));

    Set<Class<?>> targetClassSet = new HashSet<>();
    beanDefinitionSet.forEach(beanDefinition -> {
      try {
        Class<?> clazz = ClassUtils.forName(beanDefinition.getBeanClassName(), null);
        // 의존 클래스가 빠진 util 은 여기서 걸러진다
        clazz.getDeclaredMethods();
        targetClassSet.add(clazz);
      } catch (ClassNotFoundException | LinkageError e) {
        log.debug("skip dev-check util {}", beanDefinition.getBeanClassName(), e);
      }
    });

    return targetClassSet;
  }

  private DevCheckUtilInfo createDevCheckUtilInfo(Class<?> targetClass) {
    var devCheckUtilMethodInfoList = new ArrayList<DevCheckUtilMethodInfo>();
    Arrays.asList(targetClass.getDeclaredMethods()).stream()
        .filter(method ->
            Modifier.isPublic(method.getModifiers())
                && (!AnnotatedElementUtils.hasAnnotation(method, DevCheckDescription.class)
                    || (AnnotatedElementUtils.hasAnnotation(method, DevCheckDescription.class)
                        && AnnotatedElementUtils.findMergedAnnotation(
                                method, DevCheckDescription.class)
                            .displayable())))
        .forEach(method -> devCheckUtilMethodInfoList.add(createDevCheckUtilMethodInfo(method)));

    return new DevCheckUtilInfo(targetClass.getSimpleName(), devCheckUtilMethodInfoList);
  }

  private DevCheckUtilMethodInfo createDevCheckUtilMethodInfo(Method method) {
    String description = null;
    if (AnnotatedElementUtils.hasAnnotation(method, DevCheckDescription.class)) {
      var mergedAnnotation =
          AnnotatedElementUtils.findMergedAnnotation(method, DevCheckDescription.class);
      if (mergedAnnotation != null) {
        description = mergedAnnotation.value();
      }
    }

    // parameters string format
    var targetParameters = method.getParameters();
    var parameterList = new ArrayList<String>();
    if (targetParameters != null && targetParameters.length > 0) {
      for (var targetParameter : targetParameters) {
        parameterList.add(
            targetParameter.getType().getSimpleName() + " " + targetParameter.getName());
      }
    }

    return new DevCheckUtilMethodInfo(
        method.getName(), method.getReturnType().getSimpleName(), parameterList, description);
  }
}
