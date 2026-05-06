package com.timmk22.smartfarming.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    @DisplayName("Should create user with default constructor and set fields")
    void shouldCreateUserAndSetFields() {
        User user = new User();

        user.setUserId(1L);
        user.setName("Dimitar");
        user.setEmail("dimitar@test.com");
        user.setPassword("123456");

        assertThat(user.getUserId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Dimitar");
        assertThat(user.getEmail()).isEqualTo("dimitar@test.com");
        assertThat(user.getPassword()).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should initialize plantings list by default")
    void shouldInitializePlantingsListByDefault() {
        User user = new User();

        assertThat(user.getPlantings()).isNotNull();
        assertThat(user.getPlantings()).isEmpty();
    }

    @Test
    @DisplayName("getUsername should return email")
    void getUsernameShouldReturnEmail() {
        User user = new User();
        user.setEmail("dimitar@test.com");

        assertThat(user.getUsername()).isEqualTo("dimitar@test.com");
    }

    @Test
    @DisplayName("getAuthorities should return empty list")
    void getAuthoritiesShouldReturnEmptyList() {
        User user = new User();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertThat(authorities).isNotNull();
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Account status methods should return true")
    void accountStatusMethodsShouldReturnTrue() {
        User user = new User();

        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should allow setting plantings list")
    void shouldAllowSettingPlantingsList() {
        User user = new User();
        PlantingInformation planting = new PlantingInformation();

        user.getPlantings().add(planting);

        assertThat(user.getPlantings()).hasSize(1);
        assertThat(user.getPlantings().get(0)).isEqualTo(planting);
    }
}