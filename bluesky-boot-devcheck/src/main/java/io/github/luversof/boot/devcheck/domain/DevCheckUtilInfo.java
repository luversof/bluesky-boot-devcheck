package io.github.luversof.boot.devcheck.domain;

import java.util.List;

public record DevCheckUtilInfo(String methodName, List<DevCheckUtilMethodInfo> methodInfoList) {}
