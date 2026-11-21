package org.example.dao;

import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDaoTest {
    @Container
        static PostgreSQLContainer  postgres = new PostgreSQLContainer("postgres:16-alpine").
            withDatabaseName("test_users_db").
            withDatabaseName("Test").
            withPassword("Test");
    private UserDao userDao;
    @BeforeAll
    static void beforeAll(){
       HibernateUtil.initTestSessionFactory(
               postgres.getJdbcUrl(),
               postgres.getUsername(),
               postgres.getPassword()
       );
    }
    @BeforeEach
    void setUp(){
        userDao = new UserDao();
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }
    @Test
    @DisplayName("save и findById: сохраняет пользователя и находит по ID")
    void saveAndFindById(){
        User user = new User("Алексей Петров", "alex@example.com", 28);
        userDao.save(user);

        Optional<User> foundUser = userDao.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Алексей Петров", foundUser.get().getName());
        assertEquals("alex@example.com", foundUser.get().getEmail());
        assertEquals(Integer.valueOf(28), foundUser.get().getAge());
    }

    @Test
    @DisplayName("save: выбрасывает исключение при дублировании email")
    void save_duplicateEmail() {
        User user1 = new User("Анна", "duplicate@example.com", 25);
        User user2 = new User("Мария", "duplicate@example.com", 30);
        userDao.save(user1);

        assertThrows(RuntimeException.class, () -> userDao.save(user2));
    }
    @Test
    @DisplayName("save: выбрасывает исключение, если не заполнено обязательное поле name")
    void save_nullName() {
        User invalidUser = new User(null, "noname@example.com", 22);

        assertThrows(RuntimeException.class, () -> userDao.save(invalidUser));
    }

    @Test
    @DisplayName("findById: возвращает пустой Optional, если ID не найден")
    void findById_notFound() {
        Optional<User> found = userDao.findById(999999L);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("findAll: возвращает список всех пользователей")
    void findAll_returnsAllUsers() {
        userDao.save(new User("Алексей", "alex@example.com", 31));
        userDao.save(new User("Елена", "elena@example.com", 27));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
    }
    @Test
    @DisplayName("update: обновляет данные существующего пользователя")
    void update_updatesUser() {
        User user = new User("Старое Имя", "old@example.com", 20);
        userDao.save(user);

        user.setName("Новое Имя");
        user.setEmail("new@example.com");
        user.setAge(21);
        userDao.update(user);

        Optional<User> updated = userDao.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("Новое Имя", updated.get().getName());
        assertEquals("new@example.com", updated.get().getEmail());
        assertEquals(21, updated.get().getAge());
    }

    @Test
    @DisplayName("update: выбрасывает исключение при попытке занять чужой email")
    void update_duplicateEmail() {
        User user1 = new User("Первый", "first@example.com", 25);
        User user2 = new User("Второй", "second@example.com", 26);
        userDao.save(user1);
        userDao.save(user2);

        user2.setEmail("first@example.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userDao.update(user2));
        assertTrue(exception.getMessage().contains("уже занят"));
    }

    @Test
    @DisplayName("deleteById: удаляет существующего пользователя и возвращает true")
    void deleteById_removesUser() {
        User user = new User("Удаляемый", "delete@example.com", 40);
        userDao.save(user);
        Long savedId = user.getId();

        boolean deleted = userDao.deleteById(savedId);

        assertTrue(deleted);
        assertTrue(userDao.findById(savedId).isEmpty());
    }

    @Test
    @DisplayName("deleteById: возвращает false, если пользователь не найден")
    void deleteById_notFound() {
        boolean deleted = userDao.deleteById(777777L);

        assertFalse(deleted);
    }
}
