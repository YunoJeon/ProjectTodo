package com.todo.aop;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

  @Pointcut("execution(* com.todo..controller..*(..))")
  public void apiLayer() {
  }

  @Pointcut("execution(* com.todo..service..*(..))")
  public void serviceLayer() {
  }

  @Around("apiLayer()")
  public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {

    if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes)) {
      return joinPoint.proceed();
    }

    String transactionId = createTransactionId();
    HttpServletRequest originalRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

    ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(originalRequest);

    Map<String, Object> params = new HashMap<>();
    try {
      params.putAll(getParams(requestWrapper));

      String requestBody = getRequestBody(requestWrapper);
      if (requestBody != null && !requestBody.isEmpty()) {
        params.putAll(parseeBodyToMap(requestBody));
      }
    } catch (Exception e) {
      log.error("LoggerAspect error", e);
    }

    log.info("\n[{}] ▶ [Controller] {}.{} 호출 시작\n📍 HTTP Method: {}, URI: {}\n📍 Params: {}\n",
        transactionId,
        joinPoint.getSignature().getDeclaringType().getSimpleName(),
        joinPoint.getSignature().getName(),
        requestWrapper.getMethod(),
        requestWrapper.getRequestURI(),
        params);

    return joinPoint.proceed();
  }

  @Around("serviceLayer()")
  public Object logServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {

    String transactionId = getTransactionId();
    String methodName = joinPoint.getSignature().toShortString();
    Object[] args = joinPoint.getArgs();

    log.info("\n[{}] ▶ [Service] {} 호출 시작", transactionId, methodName);
    log.info("📍 매개변수: {}\n", args);

    long startTime = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long endTime = System.currentTimeMillis();

    log.info("\n[{}] ◀ [Service] {} 호출 종료, 실행 시간: {} ms\n", transactionId, methodName,
        endTime - startTime);

    return result;
  }

  private Map<String, Object> getParams(HttpServletRequest request) {

    Map<String, Object> params = new HashMap<>();
    Enumeration<String> paramNames = request.getParameterNames();

    while (paramNames.hasMoreElements()) {

      String param = paramNames.nextElement();
      String value = request.getParameter(param);

      if ("password".equalsIgnoreCase(param) || "newPassword".equalsIgnoreCase(param)) {

        value = "*****";
      }
      params.put(param, value);
    }
    return params;
  }

  private String getRequestBody(ContentCachingRequestWrapper request) {

    try {
      byte[] content = request.getContentAsByteArray();

      if (content.length > 0) {
        return new String(content, UTF_8);
      }
    } catch (Exception e) {
      log.error("요청 바디 읽기 중 오류 발생", e);
    }
    return null;
  }

  private Map<String, Object> parseeBodyToMap(String body) {

    Map<String, Object> bodyParams = new HashMap<>();

    try {
      String[] pairs = body.split("&");
      for (String pair : pairs) {
        String[] keyValue = pair.split("=");

        if (keyValue.length == 2) {

          String key = URLDecoder.decode(keyValue[0], UTF_8);
          String value = URLDecoder.decode(keyValue[1], UTF_8);

          if ("password".equalsIgnoreCase(key) || "newPassword".equalsIgnoreCase(key)) {

            value = "*****";
          }
          bodyParams.put(key, value);
        }
      }
    } catch (Exception e) {
      log.error("요청 바디 파싱 중 오류 발생", e);
    }
    return bodyParams;
  }

  private String createTransactionId() {

    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {

      String transactionId = UUID.randomUUID().toString().substring(0, 8);
      RequestContextHolder.getRequestAttributes()
          .setAttribute("transactionId", transactionId, SCOPE_REQUEST);

      return transactionId;
    } else {

      return UUID.randomUUID().toString();
    }
  }

  private String getTransactionId() {

    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

    if (attributes != null) {

      Object transactionId = attributes.getAttribute("transactionId", SCOPE_REQUEST);
      return transactionId != null ? transactionId.toString() : UUID.randomUUID().toString();
    } else {

      return UUID.randomUUID().toString();
    }
  }
}