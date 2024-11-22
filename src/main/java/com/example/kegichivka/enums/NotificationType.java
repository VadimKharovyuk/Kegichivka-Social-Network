package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    // Уведомления для соискателей
    JOB_APPLICATION_RECEIVED("Заявка на вакансию получена"),
    JOB_APPLICATION_VIEWED("Работодатель просмотрел вашу заявку"),
    JOB_APPLICATION_STATUS_CHANGED("Статус вашей заявки изменен"),
    INTERVIEW_INVITATION("Приглашение на собеседование"),
    JOB_OFFER_RECEIVED("Получено предложение о работе"),

    // Уведомления о новых вакансиях
    NEW_MATCHING_JOB("Новая вакансия по вашим критериям"),
    SAVED_SEARCH_RESULTS("Новые результаты по сохраненному поиску"),
    SAVED_JOB_DEADLINE("Срок действия сохраненной вакансии истекает"),

    // Уведомления для работодателей
    NEW_APPLICATION_RECEIVED("Получена новая заявка на вакансию"),
    CANDIDATE_PROFILE_UPDATE("Кандидат обновил свой профиль"),
    APPLICATION_RESPONSE_REQUIRED("Требуется ответ на заявку"),
    LISTING_EXPIRING("Срок публикации вакансии истекает"),
    LISTING_VIEWS_MILESTONE("Достигнут порог просмотров вакансии"),

    // Системные уведомления
    ACCOUNT_VERIFICATION("Подтверждение аккаунта"),
    PASSWORD_RESET("Сброс пароля"),
    PROFILE_INCOMPLETE("Заполните профиль"),
    DOCUMENT_VERIFICATION("Проверка документов"),
    SUBSCRIPTION_EXPIRING("Срок подписки истекает"),

    // Уведомления о взаимодействии
    NEW_MESSAGE("Новое сообщение"),
    NEW_REVIEW("Новый отзыв"),
    REVIEW_RESPONSE("Ответ на отзыв"),

    // Уведомления о событиях
    UPCOMING_INTERVIEW("Предстоящее собеседование"),
    INTERVIEW_RESCHEDULED("Собеседование перенесено"),
    INTERVIEW_CANCELLED("Собеседование отменено");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

}
