package com.example.kegichivka.model;

import com.example.kegichivka.enums.NotificationType;
import com.example.kegichivka.model.abstracts.BaseUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 4. Добавим систему уведомлений
@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RegularUser user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private LocalDateTime createdAt;

    private boolean isRead;

    @ManyToOne
    private JobListing relatedJob;
}
