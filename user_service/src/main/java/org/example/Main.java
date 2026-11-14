package org.example;

import org.example.dao.UserDao;
import org.example.ui.ConsoleMenu;
import org.example.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        ConsoleMenu menu = new ConsoleMenu(userDao);

        try {
            menu.start();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
