package com.example.base.utils;

import java.util.List;
import org.springframework.util.AntPathMatcher;

public final class RouteUtils {

  private static final AntPathMatcher pathMatcher = new AntPathMatcher();

  public static final String API_PREFIX = "/api/v1";

  public static final String AUTH_PREFIX = API_PREFIX + "/auth";
  public static final String USER_PREFIX = API_PREFIX + "/users";

  public static final List<String> whitelistedRoutes = List.of(
    API_PREFIX + "/docs/**",
    API_PREFIX + "/swagger-ui/**",
    "/api-docs/**",
    "/favicon.ico",
    "/index.html",
    "/swagger-ui/**"
  );

  public static boolean isWhitelistedRoute(String path) {
    return whitelistedRoutes.stream().anyMatch(route -> pathMatcher.match(route, path));
  }
}
