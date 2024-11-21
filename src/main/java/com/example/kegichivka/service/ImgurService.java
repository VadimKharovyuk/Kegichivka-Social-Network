package com.example.kegichivka.service;

import com.example.kegichivka.dto.ImgurResponse;
import com.example.kegichivka.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImgurService {

    @Value("${imgur.client-id}")
    private String clientId;

    @Value("${imgur.access-token}")
    private String accessToken;

    private final String IMGUR_API_URL = "https://api.imgur.com/3/image";

    private final RestTemplate restTemplate;

    public String uploadImage(byte[] imageData) {
        try {
            // Валидация входных данных
            if (imageData == null || imageData.length == 0) {
                throw new IllegalArgumentException("Image data is empty");
            }

            // Создаем заголовки
            HttpHeaders headers = new HttpHeaders();
            // Используем только Client-ID для аутентификации
            headers.set("Authorization", "Client-ID " + clientId);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Кодируем изображение в Base64
            String base64Image = Base64.getEncoder().encodeToString(imageData);

            // Создаем тело запроса
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("image", base64Image);

            // Создаем HTTP entity
            HttpEntity<MultiValueMap<String, String>> requestEntity =
                    new HttpEntity<>(body, headers);

            log.debug("Attempting to upload image to Imgur. Image size: {} bytes", imageData.length);

            // Отправляем запрос
            ResponseEntity<ImgurResponse> response = restTemplate.exchange(
                    IMGUR_API_URL,
                    HttpMethod.POST,
                    requestEntity,
                    ImgurResponse.class
            );

            // Проверяем ответ
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Imgur API returned error status: {}", response.getStatusCode());
                throw new ImageUploadException("Imgur API returned error status: " + response.getStatusCode());
            }

            ImgurResponse imgurResponse = response.getBody();
            if (imgurResponse == null) {
                throw new ImageUploadException("Received null response from Imgur");
            }

            if (!imgurResponse.isSuccess()) {
                log.error("Imgur upload failed. Response: {}", imgurResponse);
                throw new ImageUploadException("Imgur upload failed: " +
                        (imgurResponse.getData() != null ? imgurResponse.getData().getError() : "Unknown error"));
            }

            String imageUrl = imgurResponse.getData().getLink();
            log.info("Successfully uploaded image to Imgur. URL: {}", imageUrl);
            return imageUrl;

        } catch (RestClientException e) {
            log.error("RestClient error during image upload to Imgur", e);
            throw new ImageUploadException("Network error during image upload");
        } catch (Exception e) {
            log.error("Unexpected error during image upload to Imgur", e);
            throw new ImageUploadException("Unexpected error during image upload");
        }
    }

    public List<String> uploadImages(List<byte[]> imagesData) {
        if (imagesData == null || imagesData.isEmpty()) {
            return new ArrayList<>();
        }

        return imagesData.stream()
                .map(imageData -> {
                    try {
                        return uploadImage(imageData);
                    } catch (Exception e) {
                        log.error("Failed to upload image", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

