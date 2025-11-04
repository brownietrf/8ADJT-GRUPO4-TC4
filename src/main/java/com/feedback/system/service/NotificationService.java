package com.feedback.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.system.model.Feedback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para envio de notificações.
 * Chama a Cloud Function de notificação quando houver feedback urgente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${cloud.function.notification.url:http://localhost:8081/notify}")
    private String notificationFunctionUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Envia notificação de feedback urgente para a Cloud Function.
     */
    public void sendUrgentFeedbackNotification(Feedback feedback) {
        try {
            log.info("Preparando notificação para feedback urgente ID: {}", feedback.getId());

            // Monta o payload da notificação
            Map<String, Object> payload = new HashMap<>();
            payload.put("feedbackId", feedback.getId());
            payload.put("studentName", feedback.getStudentName());
            payload.put("studentEmail", feedback.getStudentEmail());
            payload.put("course", feedback.getCourse());
            payload.put("rating", feedback.getRating());
            payload.put("comment", feedback.getComment());
            payload.put("createdAt", feedback.getCreatedAt().toString());

            String jsonPayload = objectMapper.writeValueAsString(payload);

            // Cria a requisição HTTP
            RequestBody body = RequestBody.create(
                    jsonPayload,
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(notificationFunctionUrl)
                    .post(body)
                    .build();

            // Envia a requisição de forma assíncrona
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.error("Erro ao enviar notificação para Cloud Function", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        log.info("Notificação enviada com sucesso para feedback ID: {}", feedback.getId());
                    } else {
                        log.error("Erro ao enviar notificação. Status: {}", response.code());
                    }
                    response.close();
                }
            });

        } catch (Exception e) {
            log.error("Erro ao preparar notificação", e);
        }
    }

    /**
     * Envia notificação simples (para testes).
     */
    public void sendSimpleNotification(String message) {
        log.info("Notificação simples: {}", message);
        // Aqui você pode implementar envio de email, SMS, etc.
    }
}
