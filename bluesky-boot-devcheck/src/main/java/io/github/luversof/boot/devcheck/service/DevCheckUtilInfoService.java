package io.github.luversof.boot.devcheck.service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	List<DevCheckUtilInfo> devCheckUtilInfoList = new ArrayList<>();
	
	private final DevCheckProperties devCheckProperties;
	
	public DevCheckUtilInfoService(DevCheckProperties devCheckProperties) {
		super();
		this.devCheckProperties = devCheckProperties;
	}

	public List<DevCheckUtilInfo> getDevCheckUtilInfo() {
		if (!devCheckUtilInfoList.isEmpty()) {
			return devCheckUtilInfoList;
		}
		
		Set<Class<?>> targetClassSet = getTargetClassSet();
		
		targetClassSet.stream().forEach(targetClass -> devCheckUtilInfoList.add(createDevCheckUtilInfo(targetClass)));
		for (Class<?> util : targetClassSet) {
			Method[] declaredMethods = util.getDeclaredMethods();
			for (Method method : declaredMethods) {
				method.getName();
			}
		}
		return devCheckUtilInfoList;
	}
	
	private Set<Class<?>> getTargetClassSet() {
		
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(DevCheckUtil.class));
		
		var beanDefinitionSet = new HashSet<BeanDefinition>();
		devCheckProperties.getBasePackageList().forEach(basePackage -> beanDefinitionSet.addAll(provider.findCandidateComponents(basePackage)));
		
		Set<Class<?>> targetClassSet = new HashSet<>();
		beanDefinitionSet.forEach(beanDefinition -> {
			try {
				if (ClassUtils.isPresent(beanDefinition.getBeanClassName(), null)) {
					Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
					clazz.getDeclaredMethods();
					targetClassSet.add(clazz);
				}
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
			}
		});
		
		return targetClassSet;
	}
	
	private DevCheckUtilInfo createDevCheckUtilInfo(Class<?> targetClass) {
		var devCheckUtilMethodInfoList = new ArrayList<DevCheckUtilMethodInfo>();
		Arrays.asList(targetClass.getDeclaredMethods()).stream()
			.filter(method -> 
				Modifier.isPublic(method.getModifiers()) 
				&& (
					!AnnotatedElementUtils.hasAnnotation(method, DevCheckDescription.class)
					|| (AnnotatedElementUtils.hasAnnotation(method, DevCheckDescription.class) && AnnotatedElementUtils.findMergedAnnotation(method, DevCheckDescription.class).displayable())
				)
			)
			.forEach(method -> devCheckUtilMethodInfoList.add(createDevCheckUtilMethodInfo(method)));
		
		return new DevCheckUtilInfo(targetClass.getSimpleName(), devCheckUtilMethodInfoList);
	}
	
	private DevCheckUtilMethodInfo createDevCheckUtilMethodInfo(Method method) {
		String description = null;
		if (AnnotatedElementUtils.hasAnnotation(method, DevCheckDescription.class)) {
			var mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, DevCheckDescription.class);
			if (mergedAnnotation != null) {
				description = mergedAnnotation.value();
			}
		}
		
		// parameters string format
		var targetParameters = method.getParameters();
		var parameterList = new ArrayList<String>();
		if (targetParameters != null && targetParameters.length > 0) {
			for (var targetParameter : targetParameters) {
				parameterList.add(targetParameter.getType().getSimpleName() + " " + targetParameter.getName());
			}
		}
		
		return new DevCheckUtilMethodInfo(
			method.getName(),
			method.getReturnType().getSimpleName(),
			parameterList,
			description
		);
	}
}
