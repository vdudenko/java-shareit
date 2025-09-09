package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public Optional<User> getUserById(long userId) {
        return repository.findById(userId);
    }

    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }

    @Override
    public User update(Long userId, UserDto user) {
        return repository.update(userId, user);
    }

    @Override
    public void delete(long userId) {
        repository.delete(userId);
    }
}
