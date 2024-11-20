package com.example.kegichivka.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImgurResponse {
    private ImgurData data;
    private boolean success;
    private int status;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class ImgurData {
        private String id;
        private String title;
        private String description;
        private Long datetime;
        private String type;
        private boolean animated;
        private int width;
        private int height;
        private int size;
        private int views;
        private int bandwidth;
        private String vote;
        private boolean favorite;
        private String nsfw;
        private String section;
        private String accountUrl;
        private int accountId;
        private boolean isAd;
        private boolean inMostViral;
        private boolean hasSound;
        private List<String> tags;
        private int adType;
        private String adUrl;
        private String edited;
        private boolean inGallery;
        private String deletehash;
        private String name;
        private String link;

        // Дополнительные поля для ошибок
        private String error;
        private String request;
        private String method;
    }
}
