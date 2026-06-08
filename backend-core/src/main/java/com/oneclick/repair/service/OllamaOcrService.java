package com.oneclick.repair.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OllamaOcrService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ocr.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ocr.ollama.model:gemma3}")
    private String model;

    public OcrResult extract(byte[] documentBytes, String contentType, String originalFilename) {
        try {
            String prompt = "Extract invoice data from this document and return strict JSON with keys: vendorName, invoiceNumber, invoiceDate, customerName, customerPhone, subTotal, gstAmount, totalAmount, items (array of {description, quantity, unitPrice, lineTotal}), extractedText. If the file is an image or PDF, inspect the content. Do not wrap the JSON in markdown.";
            String payload = """
                    {"model":"%s","prompt":%s,"stream":false,"images":["%s"],"options":{"temperature":0}}
                    """.formatted(model, objectMapper.writeValueAsString(prompt), Base64.getEncoder().encodeToString(documentBytes));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/generate", HttpMethod.POST, new HttpEntity<>(payload, headers), String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            return new OcrResult(root.path("response").asText(), response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract OCR data from " + originalFilename + " (" + contentType + ")", e);
        }
    }

    public record OcrResult(String extractedText, String rawResponse) {}
}
