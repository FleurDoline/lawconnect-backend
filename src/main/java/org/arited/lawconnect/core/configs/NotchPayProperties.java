package org.arited.lawconnect.core.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "notchpay")
public class NotchPayProperties {
    private String baseUrl;
    private String publicKey;
    private String secretKey;
    private String hashKey;
    private String currency;
    private String callbackUrl;
}