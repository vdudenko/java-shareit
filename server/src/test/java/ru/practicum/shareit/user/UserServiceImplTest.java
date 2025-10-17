package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserByIdShouldReturnUserWhenUserExists() {
        Long userId = 1L;
        User user = new User(userId, "vadim@example.com","Vadim");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);

        assertThat(result).isPresent().contains(user);
    }

    @Test
    void getUserByIdShouldReturnEmptyWhenUserNotFound() {
        Long userId = 999L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void saveUserShouldSaveAndReturnUser() {
        User user = new User(null, "vadim@example.com", "Vadim");
        User savedUser = new User(1L, "vadim@example.com", "Vadim");

        Mockito.when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.saveUser(user);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Vadim");
        assertThat(result.getEmail()).isEqualTo("vadim@example.com");
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void updateShouldUpdateUserWhenEmailIsDifferent() {
        Long userId = 1L;
        User existingUser = new User(userId, "old@example.com", "Old Name");
        UserDto updateDto = new UserDto("new@example.com", "New Name");

        User updatedUser = new User(userId, "new@example.com", "New Name");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        User result = userService.update(userId, updateDto);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void updateShouldThrowDuplicatedDataExceptionWhenEmailIsSame() {
        Long userId = 1L;
        User existingUser = new User(userId, "same@example.com", "Name");
        UserDto updateDto = new UserDto("same@example.com", "New Name");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.update(userId, updateDto))
                .isInstanceOf(DuplicatedDataException.class)
                .hasMessageContaining("same@example.com");
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenUserNotFound() {
        Long userId = 999L;
        UserDto updateDto = new UserDto("email@example.com", "Name");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id = 999 не найден");
    }

    @Test
    void deleteShouldCallDeleteById() {
        Long userId = 1L;

        userService.delete(userId);

        Mockito.verify(userRepository).deleteById(userId);
    }
}
