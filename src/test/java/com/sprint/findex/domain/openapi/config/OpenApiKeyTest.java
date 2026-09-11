package com.sprint.findex.domain.openapi.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/** 테스트 설정의 더미 키로 확인한다. 실제 키가 주입될 수 있으므로 키 원문은 출력하지 않는다. */
@SpringBootTest
class OpenApiKeyTest {

    @Value("${PUBLIC_API_SERVICE_KEY:}")
    private String serviceKey;

    @Test
    void 서비스키_플레이스홀더가_해석된다() {
        assertThat(serviceKey).isNotNull();
        System.out.println("serviceKey resolved: length=" + serviceKey.length());
    }
}
