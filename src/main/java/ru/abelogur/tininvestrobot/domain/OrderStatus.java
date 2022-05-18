package ru.abelogur.tininvestrobot.domain;

import ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus;

public enum OrderStatus {
    NEW,
    FAILED,
    SUCCESS;

    public static OrderStatus from(OrderExecutionReportStatus status) {
        switch (status) {
            case EXECUTION_REPORT_STATUS_FILL:
                return OrderStatus.SUCCESS;
            case EXECUTION_REPORT_STATUS_REJECTED:
            case EXECUTION_REPORT_STATUS_CANCELLED:
                return OrderStatus.FAILED;
            case EXECUTION_REPORT_STATUS_NEW:
            case EXECUTION_REPORT_STATUS_PARTIALLYFILL:
                return OrderStatus.NEW;
            default:
                return null;
        }
    }
}
