package io.github.luversof.boot.devcheck.domain;

import java.util.List;

public record DevCheckUtilMethodInfo(
    String method, String returnType, List<String> parameterList, String description) {}
