package ru.itmo.soa;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

class HibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtil() {
    }

    static SessionFactory getSessionFactory(String deploymentDirectoryPath) {
        if (sessionFactory == null) {
            try {
                File file = new File("F:\\4course\\soa\\lab1\\web\\WEB-INF\\hybernate.cfg.xml");
                Configuration configuration = new Configuration().configure(file);
                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                System.out.println("Исключение!" + e);
            }
        }
        return sessionFactory;
    }
}