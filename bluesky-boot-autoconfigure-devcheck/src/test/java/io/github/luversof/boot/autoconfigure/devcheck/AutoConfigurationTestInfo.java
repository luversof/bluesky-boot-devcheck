package io.github.luversof.boot.autoconfigure.devcheck;

import java.util.HashSet;

import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;

import io.github.luversof.boot.autoconfigure.devcheck.servlet.DevCheckMvcAutoConfiguration;

public final class AutoConfigurationTestInfo {
	
	private AutoConfigurationTestInfo() {
	}

	public static final String[] BASE_PROPERTY = new String[] { "net-profile=localdev", "bluesky-boot.dev-check.enabled=true" };

	public static final Class<?>[] DEVCHECK_SERVLET_CONFIGURATION = new Class<?>[] { WebMvcAutoConfiguration.class, DispatcherServletAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class };

	public static final Class<?>[] DEVCHECK_CORE_USER_CONFIGURATION = new Class<?>[] { DevCheckAutoConfiguration.class };

	public static final Class<?>[] DEVCHECK_CORE_SERVLET_USER_CONFIGURATION = addClassAll(DEVCHECK_CORE_USER_CONFIGURATION, DevCheckMvcAutoConfiguration.class);

	@SuppressWarnings("unused")
	private static String[] addAll(String[] target, String... strings) {
		var set = new HashSet<>();

		for (var str : target) {
			set.add(str);
		}

		for (var str : strings) {
			set.add(str);
		}

		return set.toArray(new String[set.size()]);
	}

	public static Class<?>[] addClassAll(Class<?>[] target, Class<?>... classes) {
		var set = new HashSet<>();

		for (var clazz : target) {
			set.add(clazz);
		}

		for (var clazz : classes) {
			set.add(clazz);
		}
		return set.toArray(new Class<?>[set.size()]);
	}

	public static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, classes));
	}

	public static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>[] target3, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, target3, classes));
	}

	public static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>[] target3, Class<?>[] target4, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, target3, target4, classes));
	}

	public static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>[] target3, Class<?>[] target4, Class<?>[] target5, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, target3, target4, target5, classes));
	}

	public static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>[] target3, Class<?>[] target4, Class<?>[] target5, Class<?>[] target6, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, target3, target4, target5, target6, classes));
	}
}
