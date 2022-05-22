package ru.abelogur.tininvestrobot.socket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.OrderReason;

@Getter
@AllArgsConstructor
public class OrderErrorEvent {
    private String message;
    private OrderReason reason;
}
