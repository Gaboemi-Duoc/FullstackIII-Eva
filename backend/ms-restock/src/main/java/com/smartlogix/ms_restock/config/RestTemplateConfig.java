package com.smartlogix.ms_restock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración que expone un bean {@link RestTemplate} utilizado para realizar
 * llamadas HTTP hacia otros microservicios (por ejemplo, ms-inventory).
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Crea el bean {@link RestTemplate} utilizado para las comunicaciones HTTP salientes.
     *
     * @return una nueva instancia de {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
