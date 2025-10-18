package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toUser_shouldConvertUserDtoToUser() {
        Long userId = 1L;
        UserDto userDto = new UserDto("vadim@example.com", "Vadim");

        User user = userMapper.toUser(userId, userDto);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getEmail()).isEqualTo("vadim@example.com");
        assertThat(user.getName()).isEqualTo("Vadim");
    }

    @Test
    void toUser_shouldHandleNullFields() {
        // Given
        Long userId = 2L;
        UserDto userDto = new UserDto(null, null);

        User user = userMapper.toUser(userId, userDto);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getEmail()).isNull();
        assertThat(user.getName()).isNull();
    }
}