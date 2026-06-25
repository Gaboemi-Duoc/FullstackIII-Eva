package com.smartlogix.ms_user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    public void enviarCorreoBienvenida(String destinatario, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);
        headers.set("accept", "application/json");

        String htmlContent =
            "<html><body>" +
            "<h2>Hola " + username + ",</h2>" +
            "<p>Tu cuenta ha sido creada exitosamente en SmartLogix.</p>" +
            "<p>Ya puedes iniciar sesion con tu usuario y contrasena.</p>" +
            "<p>Saludos,<br>El equipo de SmartLogix</p>" +
            "</body></html>";

        Map<String, Object> body = Map.of(
            "sender", Map.of("name", senderName, "email", senderEmail),
            "to", List.of(Map.of("email", destinatario, "name", username)),
            "subject", "Bienvenido a SmartLogix",
            "htmlContent", htmlContent
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(BREVO_URL, request, String.class);
    }
}
