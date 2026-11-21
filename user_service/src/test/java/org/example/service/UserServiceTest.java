package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("createUser: создает пользователя и вызывает userDao.save()")
    void createUser_validData_shouldSaveUser() {
        User created = userService.createUser("Алексей", "alex@example.com", 25);

        assertNotNull(created);
        assertEquals("Алексей", created.getName());
        assertEquals("alex@example.com", created.getEmail());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("createUser: выбрасывает исключение при пустом имени и не вызывает DAO")
    void createUser_blankName_shouldThrowAndNotCallDao() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("   ", "alex@example.com", 25));

        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("createUser: выбрасывает исключение при некорректном email")
    void createUser_invalidEmail_shouldThrowAndNotCallDao() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Алексей", "alex-email", 25));

        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("createUser: выбрасывает исключение при отрицательном возрасте")
    void createUser_negativeAge_shouldThrowAndNotCallDao() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Алексей", "alex@example.com", -5));

        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("getUserById: возвращает пользователя, если он найден в DAO")
    void getUserById_existingId_shouldReturnUser() {
        User mockUser = new User("Анна", "anna@example.com", 30);
        when(userDao.findById(1L)).thenReturn(Optional.of(mockUser));

        User result = userService.getUserById(1L);

        assertEquals("Анна", result.getName());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getUserById: выбрасывает исключение, если пользователь не найден")
    void getUserById_notFound_shouldThrowException() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(99L));
        verify(userDao, times(1)).findById(99L);
    }

    @Test
    @DisplayName("getAllUsers: возвращает список пользователей из DAO")
    void getAllUsers_shouldReturnListFromDao() {
        List<User> mockUsers = List.of(
                new User("Алексей", "alex@example.com", 22),
                new User("Елена", "elena@example.com", 28)
        );
        when(userDao.findAll()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    @DisplayName("updateUser: обновляет все переданные поля и вызывает userDao.update()")
    void updateUser_allFields_shouldUpdateAndSave() {
        User existing = new User("Старое Имя", "old@example.com", 20);
        when(userDao.findById(1L)).thenReturn(Optional.of(existing));

        User updated = userService.updateUser(1L, "Новое Имя", "new@example.com", 21);

        assertEquals("Новое Имя", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals(Integer.valueOf(21), updated.getAge());
        verify(userDao, times(1)).update(existing);
    }

    @Test
    @DisplayName("updateUser: оставляет старые значения, если переданы пустые поля")
    void updateUser_blankFields_shouldKeepOldValues() {
        User existing = new User("Старое Имя", "old@example.com", 20);
        when(userDao.findById(1L)).thenReturn(Optional.of(existing));

        User updated = userService.updateUser(1L, "", "", null);

        assertEquals("Старое Имя", updated.getName());
        assertEquals("old@example.com", updated.getEmail());
        assertEquals(Integer.valueOf(20), updated.getAge());
        verify(userDao, times(1)).update(existing);
    }

    @Test
    @DisplayName("deleteUser: удаляет существующего пользователя")
    void deleteUser_existingId_shouldCallDeleteById() {
        when(userDao.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userDao, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser: выбрасывает исключение, если DAO вернул false")
    void deleteUser_notFound_shouldThrowException() {
        when(userDao.deleteById(55L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(55L));
        verify(userDao, times(1)).deleteById(55L);
    }
}