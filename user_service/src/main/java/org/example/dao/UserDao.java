package org.example.dao;

import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public void save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("Пользователь успешно сохранен в БД: {}", user.getEmail());
        } catch (ConstraintViolationException e) {
            rollbackTransaction(transaction);
            logger.warn("Нарушение уникальности данных при сохранении: {}", user.getEmail());
            throw new RuntimeException("Пользователь с таким email уже существует или заполнены не все обязательные поля.", e);
        } catch (HibernateException e) {
            rollbackTransaction(transaction);
            logger.error("Ошибка Hibernate при сохранении пользователя", e);
            throw new RuntimeException("Ошибка базы данных при сохранении пользователя.", e);
        }
    }

    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            logger.info("Поиск пользователя по ID {}: {}", id, user != null ? "найден" : "не найден");
            return Optional.ofNullable(user);
        } catch (HibernateException e) {
            logger.error("Ошибка Hibernate при поиске пользователя по ID {}", id, e);
            throw new RuntimeException("Ошибка базы данных при поиске пользователя.", e);
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();
            logger.info("Загружен список пользователей. Количество: {}", users.size());
            return users;
        } catch (HibernateException e) {
            logger.error("Ошибка Hibernate при получении списка пользователей", e);
            throw new RuntimeException("Ошибка базы данных при получении списка пользователей.", e);
        }
    }

    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("Данные пользователя с ID {} обновлены", user.getId());
        } catch (ConstraintViolationException e) {
            rollbackTransaction(transaction);
            logger.warn("Конфликт уникальности при обновлении пользователя ID {}", user.getId());
            throw new RuntimeException("Невозможно обновить: указанный email уже занят другим пользователем.", e);
        } catch (HibernateException e) {
            rollbackTransaction(transaction);
            logger.error("Ошибка Hibernate при обновлении пользователя ID {}", user.getId(), e);
            throw new RuntimeException("Ошибка базы данных при обновлении пользователя.", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("Пользователь с ID {} удален из БД", id);
                return true;
            }
            transaction.commit();
            logger.info("Попытка удаления: пользователь с ID {} не найден", id);
            return false;
        } catch (HibernateException e) {
            rollbackTransaction(transaction);
            logger.error("Ошибка Hibernate при удалении пользователя ID {}", id, e);
            throw new RuntimeException("Ошибка базы данных при удалении пользователя.", e);
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
                logger.info("Транзакция успешно отменена (rollback)");
            } catch (HibernateException ex) {
                logger.error("Не удалось выполнить rollback транзакции", ex);
            }
        }
    }
}