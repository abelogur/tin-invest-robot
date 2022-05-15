package ru.abelogur.tininvestrobot.strategy;

public interface InvestStrategy {

    boolean isLongSignal();

    boolean isShortSignal();

    void setLastIndex(int index);
}
