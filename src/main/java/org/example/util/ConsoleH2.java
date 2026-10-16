package org.example.util;

import org.h2.tools.Server;

import java.sql.SQLException;

/**
 * Console web H2 sur http://localhost:8082.
 * Elle tourne dans la meme JVM que l'application, c'est ce qui lui permet de voir la base en memoire.
 * Connexion : JDBC URL = jdbc:h2:mem:gestion_commerciale, utilisateur = sa, mot de passe vide.
 */
public class ConsoleH2 {
    public static final String URL = "http://localhost:8082";

    private static Server serveur;

    private ConsoleH2() {}

    public static synchronized void demarrer() {
        if (serveur != null) return;
        try {
            serveur = Server.createWebServer("-web", "-webPort", "8082").start();
            System.out.println("Console H2 disponible sur " + URL + " (JDBC URL : jdbc:h2:mem:gestion_commerciale)");
        } catch (SQLException e) {
            // port deja pris (par exemple App lancee en parallele) : l'application continue sans console
            System.err.println("Console H2 non demarree : " + e.getMessage());
        }
    }

    public static synchronized void arreter() {
        if (serveur != null) {
            serveur.stop();
            serveur = null;
        }
    }
}
