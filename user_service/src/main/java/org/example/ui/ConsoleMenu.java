package org.example.ui;

import org.example.entity.User;
import org.example.service.UserService;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final UserService userService;
    private final Scanner scanner;

    public ConsoleMenu(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            while (true) {
                printMenu();
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> getUserById();
                    case "3" -> getAllUsers();
                    case "4" -> updateUser();
                    case "5" -> deleteUser();
                    case "0" -> {
                        System.out.println("Завершение работы...");
                        return;
                    }
                    default -> System.out.println("Неверная команда. Попробуйте снова.");
                }
            }
        } finally {
            scanner.close();
        }
    }

    private void printMenu() {
        System.out.println("\n=== USER SERVICE ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void createUser() {
        try {
            System.out.print("Введите имя: ");
            String name = scanner.nextLine();

            System.out.print("Введите email: ");
            String email = scanner.nextLine();

            System.out.print("Введите возраст (или оставьте пустым): ");
            String ageInput = scanner.nextLine();
            Integer age = ageInput.isBlank() ? null : Integer.parseInt(ageInput);

            User createdUser = userService.createUser(name, email, age);
            System.out.println("Пользователь успешно создан: " + createdUser);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: возраст должен быть числом.");
        } catch (Exception e) {
            System.out.println("Не удалось создать пользователя: " + e.getMessage());
        }
    }

    private void getUserById() {
        try {
            System.out.print("Введите ID пользователя: ");
            Long id = Long.parseLong(scanner.nextLine());

            User user = userService.getUserById(id);
            System.out.println("Найден пользователь: " + user);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом.");
        } catch (Exception e) {
            System.out.println("Ошибка при поиске: " + e.getMessage());
        }
    }

    private void getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("Список пользователей пуст.");
            } else {
                System.out.println("Список всех пользователей:");
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка: " + e.getMessage());
        }
    }

    private void updateUser() {
        try {
            System.out.print("Введите ID пользователя для обновления: ");
            Long id = Long.parseLong(scanner.nextLine());

            User currentUser = userService.getUserById(id);
            System.out.println("Текущие данные: " + currentUser);

            System.out.print("Новое имя (Enter, чтобы оставить '" + currentUser.getName() + "'): ");
            String name = scanner.nextLine();

            System.out.print("Новый email (Enter, чтобы оставить '" + currentUser.getEmail() + "'): ");
            String email = scanner.nextLine();

            System.out.print("Новый возраст (Enter, чтобы оставить '" + currentUser.getAge() + "'): ");
            String ageInput = scanner.nextLine();
            Integer age = ageInput.isBlank() ? null : Integer.parseInt(ageInput);

            User updated = userService.updateUser(id, name, email, age);
            System.out.println("Данные обновлены: " + updated);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите корректное число.");
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении: " + e.getMessage());
        }
    }

    private void deleteUser() {
        try {
            System.out.print("Введите ID пользователя для удаления: ");
            Long id = Long.parseLong(scanner.nextLine());

            userService.deleteUser(id);
            System.out.println("Пользователь с ID " + id + " успешно удален.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом.");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении: " + e.getMessage());
        }
    }
}