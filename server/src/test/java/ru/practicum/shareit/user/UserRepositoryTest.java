package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByIdShouldReturnUserWhenUserExists() {
        User user = User.builder()
                .name("Vadim")
                .email("vadim@example.com")
                .build();
        User savedUser = userRepository.save(user);

        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getEmail()).isEqualTo("vadim@example.com");
    }

    @Test
    void findByIdShouldReturnEmptyWhenUserNotFound() {
        var result = userRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void saveShouldPersistUser() {
        User user = User.builder()
                .name("Alice")
                .email("alice@example.com")
                .build();

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Alice");
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void deleteByIdShouldRemoveUser() {
        User user = User.builder()
                .name("Bob")
                .email("bob@example.com")
                .build();
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void existsByIdShouldReturnTrueWhenUserExists() {
        User user = User.builder().email("test@example.com").name("Test").build();
        User saved = userRepository.save(user);
        assertThat(userRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    void existsByIdShouldReturnFalseWhenUserDoesNotExist() {
        assertThat(userRepository.existsById(999L)).isFalse();
    }
}