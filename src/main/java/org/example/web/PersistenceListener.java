package org.example.web;

import org.example.util.ConsoleH2;
import org.example.util.JeuDeDonnees;
import org.example.util.JpaUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Accompagne le cycle de vie de l'application web :
 * au deploiement on prepare la base et la console H2, a l'arret on ferme tout.
 */
public class PersistenceListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // creation anticipee de l'EntityManagerFactory : la premiere requete HTTP n'en paie pas le cout
        JpaUtil.getEntityManagerFactory();
        JeuDeDonnees.charger();
        ConsoleH2.demarrer();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConsoleH2.arreter();
        JpaUtil.shutdown();
    }
}
