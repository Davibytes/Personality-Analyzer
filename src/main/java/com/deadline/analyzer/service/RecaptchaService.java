package com.deadline.analyzer.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret.key}")
    private String recaptchaSecretKey;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final double SCORE_THRESHOLD = 0.5;

    public boolean verifyRecaptcha(String token) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            // Create request body
            String requestBody = "secret=" + recaptchaSecretKey + "&response=" + token;

            // Create POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RECAPTCHA_VERIFY_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send request
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            // Parse response
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);

            // Check if verification was successful
            boolean success = jsonObject.get("success").getAsBoolean();
            double score = jsonObject.get("score").getAsDouble();

            System.out.println("reCAPTCHA Score: " + score);

            // Return true if successful AND score is above threshold
            return success && score >= SCORE_THRESHOLD;

        } catch (Exception e) {
            System.out.println("reCAPTCHA verification error: " + e.getMessage());
            return false;
        }
    }
}