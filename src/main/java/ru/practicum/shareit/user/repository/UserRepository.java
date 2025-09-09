package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long userId);

    User save(User user);

    User update(Long userId, UserDto user);

    void delete(long userId);

    boolean isUserEmailExist(String userEmail);

    Optional<User> findByEmail(String userEmail);

    boolean isUserExist(long userId);
}
