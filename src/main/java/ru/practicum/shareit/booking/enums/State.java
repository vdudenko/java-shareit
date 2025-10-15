package ru.practicum.shareit.booking.enums;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State from(String stateStr) {
//        if (stateStr == null || stateStr.isBlank()) {
//            return ALL; // или бросить исключение, если null/пусто недопустимо
//        }
        try {
            return State.valueOf(stateStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + stateStr);

        }
    }
}
