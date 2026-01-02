package com.example.base.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ContentLanguageFilter extends OncePerRequestFilter {

  LocaleResolver localeResolver;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    try {
      Locale locale = localeResolver.resolveLocale(request);
      LocaleContextHolder.setLocale(locale);
      response.setHeader(HttpHeaders.CONTENT_LANGUAGE, locale.toLanguageTag());
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      LocaleContextHolder.resetLocaleContext();
    }
  }
}
