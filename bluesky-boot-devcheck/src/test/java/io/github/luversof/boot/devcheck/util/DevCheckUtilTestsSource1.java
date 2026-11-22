package io.github.luversof.boot.devcheck.util;

public enum DevCheckUtilTestsSource1 {
  CASE_A(
      new String[] {"/_check", "/test/_check", "/user/_check"},
      "/_check/asdf/asdf/sadf",
      "/_check"),
  CASE_B(
      new String[] {"/_check", "/test/_check", "/user/_check"},
      "/test/_check/asdf/asdf/sadf",
      "/test/_check"),
  CASE_C(
      new String[] {"/_check", "/test/_check", "/test/test2/_check"},
      "/test/_check/asdf/asdf/sadf",
      "/test/_check"),
  CASE_D(
      new String[] {"/_check", "/test/_check", "/test/test2/_check"},
      "/test/test2/_check/asdf/asdf/sadf",
      "/test/test2/_check"),
  // 한 접두어가 다른 접두어의 앞부분이면 긴 쪽이 맞다
  CASE_E(new String[] {"/_check", "/_check2"}, "/_check2/asdf", "/_check2"),
  // 패턴 prefix 도 찾는다 - 예전에는 문자열 비교라 null 이었다
  CASE_F(new String[] {"/_check", "/{site}/_check"}, "/kr/_check/index", "/{site}/_check");

  private String[] pathPrefixes;

  private String currentRequestURL;

  private String expectedPathPrefix;

  private DevCheckUtilTestsSource1(
      String[] pathPrefixes, String currentRequestURL, String expectedPathPrefix) {
    this.pathPrefixes = pathPrefixes;
    this.currentRequestURL = currentRequestURL;
    this.expectedPathPrefix = expectedPathPrefix;
  }

  public String[] getPathPrefixes() {
    return pathPrefixes;
  }

  public String getCurrentRequestURL() {
    return currentRequestURL;
  }

  public String getExpectedPathPrefix() {
    return expectedPathPrefix;
  }
}
