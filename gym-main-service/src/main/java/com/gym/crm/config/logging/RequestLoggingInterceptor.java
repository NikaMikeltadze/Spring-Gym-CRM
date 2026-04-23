    package com.gym.crm.config.logging;

    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.lang.NonNull;
    import org.springframework.stereotype.Component;
    import org.springframework.web.servlet.HandlerInterceptor;

    import java.util.Arrays;
    import java.util.Map;
    import java.util.stream.Collectors;

    @Slf4j
    @Component
    public class RequestLoggingInterceptor implements HandlerInterceptor {

        public static final String ERROR_MESSAGE_ATTRIBUTE = "loggedErrorMessage";

        @Override
        public void afterCompletion(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull Object handler,
                                    Exception ex) {
            String endpoint = request.getMethod() + " " + request.getRequestURI();
            String requestSummary = buildRequestSummary(request);
            int status = response.getStatus();
            String errorMessage = resolveErrorMessage(request, ex);

            if (status >= HttpServletResponse.SC_BAD_REQUEST) {
                log.warn("REST call completed: endpoint={}, request={}, status={}, error={}",
                        endpoint, requestSummary, status, errorMessage);
                return;
            }

            log.info("REST call completed: endpoint={}, request={}, status={}, error={}",
                    endpoint, requestSummary, status, errorMessage);
        }

        private String resolveErrorMessage(HttpServletRequest request, Exception ex) {
            Object messageFromHandler = request.getAttribute(ERROR_MESSAGE_ATTRIBUTE);
            if (messageFromHandler != null) {
                return String.valueOf(messageFromHandler);
            }
            if (ex != null && ex.getMessage() != null && !ex.getMessage().isBlank()) {
                return ex.getMessage();
            }
            return "-";
        }

        private String buildRequestSummary(HttpServletRequest request) {
            String query = request.getQueryString() == null ? "" : request.getQueryString();
            String params = summarizeParameters(request.getParameterMap());
            return "query=" + query + ", params=" + params;
        }

        private String summarizeParameters(Map<String, String[]> parameterMap) {
            if (parameterMap == null || parameterMap.isEmpty()) {
                return "{}";
            }

            return parameterMap.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + formatValue(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(", ", "{", "}"));
        }

        private String formatValue(String key, String[] values) {
            if (isSensitiveKey(key)) {
                return "***";
            }
            if (values == null) {
                return "null";
            }
            if (values.length == 1) {
                return values[0];
            }
            return Arrays.toString(values);
        }

        private boolean isSensitiveKey(String key) {
            String normalized = key.toLowerCase();
            return normalized.contains("password")
                    || normalized.contains("secret")
                    || normalized.contains("token");
        }
    }
