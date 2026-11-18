package io.github.luversof.boot.devcheck.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.CollectionUtils;

public class DevCheckUtil {

  private DevCheckUtil() {}

  /**
   * 현재 요청에 해당하는 pathPrefix를 반환
   *
   * @param pathPrefixes
   * @param currentRequestURI
   * @return
   */
  public static String getCurrentPathPrefix(String[] pathPrefixes, String currentRequestURI) {
    if (pathPrefixes == null || currentRequestURI == null) {
      return null;
    }

    if (pathPrefixes.length == 1) {
      return pathPrefixes[0];
    }

    // 가장 긴 것 - /_check 와 /_check2 가 둘 다 /_check2/x 의 앞부분이므로 짧은 것을 고르면 틀린다
    return Arrays.stream(pathPrefixes)
        .filter(currentRequestURI::startsWith)
        .max(Comparator.comparingInt(String::length))
        .orElse(null);
  }

  /**
   * 대상 urlList를 pathPrefix에 해당하는 우선으로 정렬
   *
   * @param urlList
   * @param pathPrefix
   */
  public static void sortUrlList(List<String> urlList, String pathPrefix) {
    if (CollectionUtils.isEmpty(urlList) || pathPrefix == null) {
      return;
    }
    urlList.sort(
        Comparator.comparing((String target) -> !target.startsWith(pathPrefix))
            .thenComparingInt(String::length));
  }

  public static void sortUrlList(
      List<String> urlList, String[] pathPrefixes, String currentRequestURI) {
    if (CollectionUtils.isEmpty(urlList) || pathPrefixes == null || currentRequestURI == null) {
      return;
    }

    sortUrlList(urlList, getCurrentPathPrefix(pathPrefixes, currentRequestURI));
  }
}
