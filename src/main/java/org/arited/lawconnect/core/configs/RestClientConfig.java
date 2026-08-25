package org.arited.lawconnect.core.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient notchPayRestClient(NotchPayProperties properties) {
        System.out.println(">>> NotchPay publicKey utilisée = [" + properties.getPublicKey() + "]");
        System.out.println(">>> NotchPay callbackUrl utilisée = [" + properties.getCallbackUrl() + "]");
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
               // .defaultHeader("Authorization", properties.getSecretKey())
                .defaultHeader("Authorization", properties.getPublicKey())

                .build();
    }
}
