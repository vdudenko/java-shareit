package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void builderShouldCreateUserWithAllFields() {
        User user = User.builder()
                .id(1L)
                .email("vadim@example.com")
                .name("Vadim")
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("vadim@example.com");
        assertThat(user.getName()).isEqualTo("Vadim");
    }

    @Test
    void updateUserShouldUpdateEmailAndName_whenDtoFieldsAreNotNull() {
        User user = User.builder()
                .email("old@example.com")
                .name("Old Name")
                .build();

        UserDto dto = new UserDto("new@example.com", "New Name");

        user.updateUser(dto);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getName()).isEqualTo("New Name");
    }

    @Test
    void updateUserShouldNotUpdateFields_whenDtoFieldsAreNull() {
        User user = User.builder()
                .email("existing@example.com")
                .name("Existing Name")
                .build();

        UserDto dto = new UserDto(null, null);

        user.updateUser(dto);

        assertThat(user.getEmail()).isEqualTo("existing@example.com");
        assertThat(user.getName()).isEqualTo("Existing Name");
    }

    @Test
    void updateUserShouldUpdateOnlyEmailWhenNameIsNull() {
        User user = User.builder()
                .email("old@example.com")
                .name("Old Name")
                .build();

        UserDto dto = new UserDto("new@example.com", null);

        user.updateUser(dto);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getName()).isEqualTo("Old Name");
    }

    @Test
    void updateUserShouldUpdateOnlyNameWhenEmailIsNull() {
        User user = User.builder()
                .email("old@example.com")
                .name("Old Name")
                .build();

        UserDto dto = new UserDto(null, "New Name");

        user.updateUser(dto);

        assertThat(user.getEmail()).isEqualTo("old@example.com");
        assertThat(user.getName()).isEqualTo("New Name");
    }

    @Test
    void equalsShouldReturnTrueWhenIdsAreEqual() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(1L).build();

        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void equalsShouldReturnFalseWhenIdsAreDifferent() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void equalsShouldReturnFalseWhenOtherObjectIsNotUser() {
        User user = User.builder().id(1L).build();
        String notUser = "not a user";

        assertThat(user).isNotEqualTo(notUser);
    }

    @Test
    void equalsShouldReturnFalseWhenIdIsNull() {
        User user1 = User.builder().id(null).build();
        User user2 = User.builder().id(null).build();

        assertThat(user1).isNotEqualTo(user2); // по вашей логике: id != null → false
    }

    @Test
    void hashCodeShouldBeBasedOnId() {
        User user = User.builder().id(42L).build();
        assertThat(user.hashCode()).isEqualTo(42);
    }
}
