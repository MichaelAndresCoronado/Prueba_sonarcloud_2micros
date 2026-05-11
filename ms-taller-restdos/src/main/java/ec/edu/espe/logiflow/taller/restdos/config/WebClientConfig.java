package ec.edu.espe.logiflow.taller.restdos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient flotaWebClient(@Value("${logiflow.services.flota.base-url}") String flotaBaseUrl) {
        return WebClient.builder()
                .baseUrl(flotaBaseUrl)
                .build();
    }
}
