package ru.practicum.shareit.booking.enums;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State from(String stateStr) {
        try {
            return State.valueOf(stateStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + stateStr);

        }
    }
}
