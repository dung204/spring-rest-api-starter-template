package com.example.base.advices;

import com.example.base.dtos.ResponseDTO;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class ResponseTranslatorAdvice implements ResponseBodyAdvice<Object> {

  MessageSource messageSource;

  @Override
  public boolean supports(
    MethodParameter returnType,
    Class<? extends HttpMessageConverter<?>> converterType
  ) {
    var rawReturnType = returnType.getParameterType();

    if (ResponseDTO.class.isAssignableFrom(rawReturnType)) {
      return true;
    }

    if (ResponseEntity.class.isAssignableFrom(rawReturnType)) {
      var bodyType = ResolvableType.forMethodParameter(returnType).getGeneric(0).resolve();
      return bodyType != null && ResponseDTO.class.isAssignableFrom(bodyType);
    }

    return false;
  }

  @Override
  public Object beforeBodyWrite(
    Object body,
    MethodParameter returnType,
    MediaType selectedContentType,
    Class<? extends HttpMessageConverter<?>> selectedConverterType,
    ServerHttpRequest request,
    ServerHttpResponse response
  ) {
    if (body instanceof ResponseDTO responseDTO) {
      String messageKey = responseDTO.getMessage();
      Locale locale = LocaleContextHolder.getLocale();

      try {
        String translatedMessage = messageSource.getMessage(messageKey, null, locale);
        responseDTO.setMessage(translatedMessage);
      } catch (NoSuchMessageException e) {
        log.warn(
          "There is no such message for key '{}'. The message will be remained as is.",
          messageKey
        );
      }
    }

    return body;
  }
}
