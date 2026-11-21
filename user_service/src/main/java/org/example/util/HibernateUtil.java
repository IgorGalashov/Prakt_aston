package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory(Properties customProperties) {
        try {
            Configuration configuration = new Configuration().configure();
            if (customProperties != null && !customProperties.isEmpty()) {
                configuration.addProperties(customProperties);
            }
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Ошибка инициализации SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory(null);
        }
        return sessionFactory;
    }

    public static void initTestSessionFactory(String url, String username, String password) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        Properties props = new Properties();
        props.setProperty("hibernate.connection.url", url);
        props.setProperty("hibernate.connection.username", username);
        props.setProperty("hibernate.connection.password", password);
        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        sessionFactory = buildSessionFactory(props);
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
