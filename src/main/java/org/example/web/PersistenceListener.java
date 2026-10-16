package org.example.web;

import org.example.util.HibernateUtil;
import org.example.util.JpaUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Libere les ressources de persistance a l'arret de l'application web.
 */
public class PersistenceListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // ouvre l'EntityManagerFactory au demarrage plutot qu'a la premiere requete
        JpaUtil.getEntityManagerFactory();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JpaUtil.shutdown();
        HibernateUtil.shutdown();
    }
}
