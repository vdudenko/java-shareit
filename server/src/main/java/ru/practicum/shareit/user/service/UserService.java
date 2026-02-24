package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(long userId);

    User saveUser(User user);

    User update(Long userId, UserDto user);

    void delete(long userId);
}
