package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    @Test
    @DisplayName("Should save user and find by id")
    void shouldSaveUserAndFindById() {
        User user = createUser("Dimitar", "dimitar@test.com", "123456");

        User saved = userRepository.saveAndFlush(user);
        Optional<User> found = userRepository.findById(saved.getUserId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("dimitar@test.com");
    }

    @Test
    void shouldFindUserByEmail() {
        User user = createUser("Dimitar", "findme@test.com", "123456");
        userRepository.saveAndFlush(user);

        assertThat(userRepository.findByEmail("findme@test.com")).isPresent();
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        User user = createUser("Dimitar", "exists@test.com", "123456");
        userRepository.saveAndFlush(user);

        assertThat(userRepository.existsByEmail("exists@test.com")).isTrue();
    }

    @Test
    void shouldDeleteUser() {
        User user = createUser("Delete", "delete@test.com", "123456");
        User saved = userRepository.saveAndFlush(user);

        userRepository.deleteById(saved.getUserId());
        userRepository.flush();

        assertThat(userRepository.findById(saved.getUserId())).isEmpty();
    }

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        userRepository.saveAndFlush(createUser("User1", "same@test.com", "123456"));

        assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.saveAndFlush(createUser("User2", "same@test.com", "654321"))
        );
    }
}