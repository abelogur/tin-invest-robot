package ru.abelogur.tininvestrobot.service.notification.socket;

import lombok.Getter;

@Getter
public enum SocketOrderEvents {
    NEW("new-order"),
    SUCCESSFUL("successful-order"),
    FAILED("failed-order"),
    ERROR("error-order");

    private final String topic;

    SocketOrderEvents(String topic) {
        this.topic = topic;
    }
}
