package io.github.luversof.boot.devcheck.service.servlet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import io.github.luversof.boot.devcheck.DevCheckProperties;
import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import io.github.luversof.boot.devcheck.domain.DevCheckInfo;
import io.github.luversof.boot.devcheck.util.DevCheckUtil;

public class DevCheckInfoMvcService implements ApplicationContextAware {

  /** path prefix &rarr; 목록. 요청 스레드 여럿이 동시에 채울 수 있다. */
  private final Map<String, List<DevCheckInfo>> devCheckInfoListMap = new ConcurrentHashMap<>();

  private ApplicationContext applicationContext;

  private final ParameterNameDiscoverer parameterNameDiscoverer =
      new DefaultParameterNameDiscoverer();

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public List<DevCheckInfo> getDevCheckInfoList() {
    var devCheckProperties = applicationContext.getBean(DevCheckProperties.class);
    var currentPathPrefix =
        DevCheckUtil.getCurrentPathPrefix(
            devCheckProperties.getPathPrefixes(), currentPathWithinApplication());
    return devCheckInfoListMap.computeIfAbsent(
        currentPathPrefix == null ? "" : currentPathPrefix, prefix -> createDevCheckInfoList());
  }

  private List<DevCheckInfo> createDevCheckInfoList() {
    var devCheckInfoList = new ArrayList<DevCheckInfo>();

    var handlerMethodMap = getTargetHandlerMethodMap();
    handlerMethodMap.entrySet().stream()
        .filter(map -> {
          var description = map.getValue().getMethodAnnotation(DevCheckDescription.class);
          return description == null || description.displayable();
        })
        .forEach(map -> devCheckInfoList.add(createDevCheckInfo(map)));
    return devCheckInfoList.stream()
        .sorted(
            Comparator.comparing(DevCheckInfo::beanName)
                .thenComparing(devCheckInfo -> devCheckInfo.urlList().get(0)))
        .toList();
  }

  /**
   * get handlerMethod map from RequestMappingHAndlerMapping bean
   *
   * @return
   */
  private Map<RequestMappingInfo, HandlerMethod> getHandlerMethodMap() {
    var requestMappingHandlerMappingMap =
        applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);

    return requestMappingHandlerMappingMap.values().stream()
        .map(RequestMappingHandlerMapping::getHandlerMethods)
        .map(Map::entrySet)
        .flatMap(Collection::stream)
        // 같은 매핑이 handler mapping 여럿에 있으면 첫 것을 쓴다(예전에는 중복 키로 예외가 났다)
        .collect(
            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (first, second) -> first));
  }

  /**
   * get target handlerMethod map
   *
   * @return
   */
  private Map<RequestMappingInfo, HandlerMethod> getTargetHandlerMethodMap() {
    var pathPrefixes = applicationContext.getBean(DevCheckProperties.class).getPathPrefixes();
    // getPatternValues 는 PathPattern·AntPathMatcher 두 방식 모두에서 채워진다
    return getHandlerMethodMap().entrySet().stream()
        .filter(handlerMapping ->
            handlerMapping.getValue().getBeanType().isAnnotationPresent(DevCheckController.class)
                && handlerMapping.getKey().getPatternValues().stream()
                    .anyMatch(pattern -> Arrays.stream(pathPrefixes).anyMatch(pattern::startsWith))
                && handlerMapping.getKey().getProducesCondition().getExpressions().stream()
                    .anyMatch(mediaTypeExpression ->
                        mediaTypeExpression.getMediaType().equals(MediaType.APPLICATION_JSON)))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  private DevCheckInfo createDevCheckInfo(
      Entry<RequestMappingInfo, HandlerMethod> handlerMethodMap) {
    var requestMappingInfo = handlerMethodMap.getKey();
    var handlerMethod = handlerMethodMap.getValue();

    // 구하려고 하는 값 : beanName, method, urlList, description
    String beanName = handlerMethod.getBean().toString();
    String method = handlerMethod.getMethod().getName();
    String description = null;

    var methodAnnotation = handlerMethod.getMethodAnnotation(DevCheckDescription.class);
    if (methodAnnotation != null) {
      description = methodAnnotation.value();
    }

    // 현재 요청의 path prefix 주소를 앞에 둔다 - 컨텍스트 경로를 붙이기 전에 비교해야 맞는다
    var patterns = new ArrayList<>(requestMappingInfo.getPatternValues());
    DevCheckUtil.sortUrlList(
        patterns,
        applicationContext.getBean(DevCheckProperties.class).getPathPrefixes(),
        currentPathWithinApplication());
    var urlList =
        patterns.stream()
            .map(pattern -> getUrlWithParameter(pattern, handlerMethod.getMethod()))
            .toList();

    return new DevCheckInfo(beanName, method, urlList, description);
  }

  /** 컨텍스트 경로(/app 등)를 뺀 현재 요청 경로. path prefix 는 이 경로 기준이다. */
  private static String currentPathWithinApplication() {
    var request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    return UrlPathHelper.defaultInstance.getPathWithinApplication(request);
  }

  public String getUrlWithParameter(String pattern, Method method) {
    var stringBuilder = new StringBuilder();
    stringBuilder.append(
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest()
            .getContextPath());
    stringBuilder.append(pattern);

    var parameterNames = parameterNameDiscoverer.getParameterNames(method);
    if (parameterNames != null) {
      var parameters = method.getParameters();
      // 첫 query 파라미터에 "?" - 앞쪽 path variable 을 건너뛰어도 그렇다
      var separator = "?";
      for (int i = 0; i < parameterNames.length; i++) {
        if (parameters[i].isAnnotationPresent(PathVariable.class)) {
          continue;
        }
        stringBuilder
            .append(separator)
            .append(parameterNames[i])
            .append("={")
            .append(parameterNames[i])
            .append("}");
        separator = "&";
      }
    }

    return stringBuilder.toString().replace("//", "/");
  }
}
