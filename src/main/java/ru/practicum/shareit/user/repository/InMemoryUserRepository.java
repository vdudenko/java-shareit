package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User save(User user) {
        if (isUserEmailExist(user.getEmail())) {
            throw new DuplicateFormatFlagsException("Этот имейл уже используется - " + user.getEmail());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long userId, UserDto user) {
        if (isUserExist(userId)) {
            User oldUser = users.get(userId);

            if (user.getEmail() != null) {
                if (isUserEmailExist(user.getEmail())) {
                    throw new DuplicatedDataException("Этот имейл уже используется - " + user.getEmail());
                }
                oldUser.setEmail(user.getEmail());
            }

            if (user.getName() != null) {
                oldUser.setName(user.getName());
            }

            return oldUser;
        }

        throw new NotFoundException("Пользователь с id = " + userId + " не найден");
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    @Override
    public boolean isUserEmailExist(String userEmail) {
        return findByEmail(userEmail).isPresent();
    }

    @Override
    public Optional<User> findByEmail(String userEmail) {
        for (User u : users.values()) {
            if (u.getEmail().equals(userEmail)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isUserExist(long userId) {
        return users.containsKey(userId);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
