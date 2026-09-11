package com.sprint.findex.domain.openapi.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiRestClientConfig {

    @Bean(name = "openApiRestClient")
    public RestClient openApiRestClient(OpenApiProperties properties) {
        return createRestClient(properties);
    }

    public static RestClient createRestClient(OpenApiProperties properties) {
        validate(properties);

        HttpClient httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(properties.getConnectTimeout())
                        .followRedirects(HttpClient.Redirect.NEVER)
                        .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getCallTimeout());

        DefaultUriBuilderFactory uriBuilderFactory =
                new DefaultUriBuilderFactory(properties.getUri());
        // 클라이언트가 쿼리 값을 이미 인코딩하므로 이중 인코딩을 막는다.
        uriBuilderFactory.setEncodingMode(EncodingMode.NONE);

        return RestClient.builder()
                .uriBuilderFactory(uriBuilderFactory)
                .requestFactory(requestFactory)
                .build();
    }

    private static void validate(OpenApiProperties properties) {
        requireText(properties.getUri(), "findex.openapi.uri", "주가지수 오퍼레이션 전체 URL을 설정하세요.");
        requirePositive(properties.getConnectTimeout(), "findex.openapi.connect-timeout");
        requirePositive(properties.getCallTimeout(), "findex.openapi.call-timeout");
    }

    private static void requireText(String value, String name, String hint) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " 가 비어 있습니다. " + hint);
        }
    }

    private static void requirePositive(Duration duration, String name) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            throw new IllegalStateException(name + " 은(는) 0보다 큰 값이어야 합니다.");
        }
    }
}
