package org.example;

import org.example.dao.UserDao;
import org.example.service.UserService;
import org.example.ui.ConsoleMenu;
import org.example.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        UserService userService = new UserService(userDao);
        ConsoleMenu menu = new ConsoleMenu(userService);

        try {
            menu.start();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}