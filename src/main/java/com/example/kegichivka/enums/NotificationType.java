package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    // Уведомления для соискателей
    JOB_APPLICATION_RECEIVED("Заявка на вакансію отримана"),
    JOB_APPLICATION_VIEWED("Роботодавець переглянув вашу заявку"),
    JOB_APPLICATION_STATUS_CHANGED("Статус вашої заявки змінено"),
    INTERVIEW_INVITATION("Запрошення на співбесіду"),
    JOB_OFFER_RECEIVED("Отримано пропозицію роботи"),

    // Уведомления о новых вакансиях
    NEW_MATCHING_JOB("Нова вакансія за вашими критеріями"),
    SAVED_SEARCH_RESULTS("Нові результати збереженого пошуку"),
    SAVED_JOB_DEADLINE("Термін дії збереженої вакансії закінчується"),

    // Уведомления для работодателей
    NEW_APPLICATION_RECEIVED("Отримано нову заявку на вакансію"),
    CANDIDATE_PROFILE_UPDATE("Кандидат оновив свій профіль"),
    APPLICATION_RESPONSE_REQUIRED("Необхідно відповісти на заявку"),
    LISTING_EXPIRING("Термін публікації вакансії спливає"),
    LISTING_VIEWS_MILESTONE("Досягнуто поріг переглядів вакансії"),

    // Системные уведомления
    ACCOUNT_VERIFICATION("Підтвердження акаунта"),
    PASSWORD_RESET("Скидання пароля"),
    PROFILE_INCOMPLETE("Заповніть профіль"),
    DOCUMENT_VERIFICATION("Перевірка документів"),
    SUBSCRIPTION_EXPIRING("Термін підписки спливає"),

    // Уведомления о взаимодействии
    NEW_MESSAGE("Нове повідомлення"),
    NEW_REVIEW("Новий відгук"),
    REVIEW_RESPONSE("Відповідь на відгук"),

    // Уведомления о событиях
    UPCOMING_INTERVIEW("Наближається співбесіда"),
    INTERVIEW_RESCHEDULED("Співбесіда перенесена"),
    INTERVIEW_CANCELLED("Співбесіда скасована");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }
}
