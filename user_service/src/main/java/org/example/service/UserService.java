package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;

    public UserService() {
        this(new UserDao());
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, Integer age) {
        validateUserData(name, email, age);
        User user = new User(name.trim(), email.trim(), age);
        userDao.save(user);
        logger.info("Сервис: создан пользователь {}", user.getEmail());
        return user;
    }

    public User getUserById(Long id) {
        validateId(id);
        return userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + id + " не найден"));
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String newName, String newEmail, Integer newAge) {
        validateId(id);
        User existingUser = getUserById(id);

        if (newName != null && !newName.isBlank()) {
            existingUser.setName(newName.trim());
        }
        if (newEmail != null && !newEmail.isBlank()) {
            if (!newEmail.contains("@")) {
                throw new IllegalArgumentException("Некорректный формат email");
            }
            existingUser.setEmail(newEmail.trim());
        }
        if (newAge != null) {
            if (newAge < 0 || newAge > 150) {
                throw new IllegalArgumentException("Возраст должен быть в диапазоне от 0 до 150 лет");
            }
            existingUser.setAge(newAge);
        }

        userDao.update(existingUser);
        logger.info("Сервис: обновлен пользователь с ID {}", id);
        return existingUser;
    }

    public void deleteUser(Long id) {
        validateId(id);
        boolean isDeleted = userDao.deleteById(id);
        if (!isDeleted) {
            throw new IllegalArgumentException("Невозможно удалить: пользователь с ID " + id + " не найден");
        }
        logger.info("Сервис: удален пользователь с ID {}", id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }
    }

    private void validateUserData(String name, String email, Integer age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        if (email == null || !email.contains("@") || email.isBlank()) {
            throw new IllegalArgumentException("Некорректный формат email");
        }
        if (age != null && (age < 0 || age > 150)) {
            throw new IllegalArgumentException("Возраст должен быть в диапазоне от 0 до 150 лет");
        }
    }
}
