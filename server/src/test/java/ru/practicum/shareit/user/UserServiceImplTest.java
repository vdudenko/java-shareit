package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void getUserByIdShouldReturnNull() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(1L);
        assertThat(result).isEmpty();
    }

    @Test
    void getUserById() {
        User expectedUser = User.builder().id(1L).name("Vadim").email("dudenko.vadim@google.com").build();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedUser));

        Optional<User> actual = userService.getUserById(1L);
        assertThat(actual).contains(expectedUser);
    }

    @Test
    void saveUser() {
        User inputUser = User.builder().name("Vadim").email("dudenko.vadim@google.com").build();
        User savedUser = User.builder().id(1L).name("Vadim").email("dudenko.vadim@google.com").build();
        User expectedUser = User.builder().id(1L).name("Vadim").email("dudenko.vadim@google.com").build();

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        User actualUser = userService.saveUser(inputUser);
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void updateUserShouldReturnNotFoundException() {
        User user = User.builder().id(1L).name("Vadim").email("dudenko.vadim@google.com").build();
        UserDto userDto = new UserDto("Vadim test", "dudenko.vadim@google.com");
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(1L, userDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update() {
        Long userId = 1L;
        UserDto userDto = new UserDto("dudenko@google.com","Vadim test");
        User existingUser = User.builder()
                .id(userId)
                .name("Vadim")
                .email("dudenko.vadim@google.com")
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);
        User actual = userService.update(1L, userDto);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getName()).isEqualTo("Vadim test");
        assertThat(actual.getEmail()).isEqualTo("dudenko@google.com");
    }

    @Test
    void delete() {
        User existingUser = User.builder()
                .id(1L)
                .name("Vadim")
                .email("dudenko.vadim@google.com")
                .build();
        userService.delete(existingUser.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }
}
