package com.oneclick.repair.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EvolutionWhatsAppService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${evolution.api.base-url:http://localhost:8082}")
    private String baseUrl;

    @Value("${evolution.api.instance:oneclick_instance}")
    private String instance;

    @Value("${evolution.api.key:}")
    private String apiKey;

    public JsonNode sendText(String number, String text) {
        Map<String, Object> body = new HashMap<>();
        body.put("number", normalizeNumber(number));
        body.put("text", text);
        body.put("delay", 0);
        body.put("linkPreview", true);
        return post("/message/sendText/" + instance, body);
    }

    public JsonNode sendDocument(String number, String mediaUrl, String fileName, String caption, String mimeType) {
        Map<String, Object> body = new HashMap<>();
        body.put("number", normalizeNumber(number));
        body.put("mediatype", "document");
        body.put("mimetype", mimeType != null ? mimeType : "application/pdf");
        body.put("caption", caption);
        body.put("media", mediaUrl);
        body.put("fileName", fileName);
        body.put("delay", 0);
        body.put("linkPreview", false);
        return post("/message/sendMedia/" + instance, body);
    }

    private JsonNode post(String path, Map<String, Object> body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("apikey", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new RuntimeException("Evolution API request failed: " + response.statusCode() + " " + response.body());
            }
            return objectMapper.readTree(response.body());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Unable to call Evolution API", e);
        }
    }

    private String normalizeNumber(String number) {
        return number == null ? "" : number.replaceAll("\\D+", "");
    }
}
