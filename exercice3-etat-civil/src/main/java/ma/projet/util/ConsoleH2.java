package ma.projet.util;

import org.h2.tools.Server;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Console web H2 (http://localhost:8082) lancee par les programmes de test.
 * Elle doit tourner dans la meme JVM que l'application pour voir la base en memoire.
 */
public class ConsoleH2 {
    public static final String ADRESSE = "http://localhost:8082";

    private static Server serveur;

    private ConsoleH2() {}

    public static void demarrer(String nomBase) {
        try {
            serveur = Server.createWebServer("-web", "-webPort", "8082").start();
            System.out.println("Console H2 : " + ADRESSE + "  |  JDBC URL : jdbc:h2:mem:" + nomBase
                    + "  |  utilisateur : sa  |  mot de passe : (vide)");
        } catch (SQLException e) {
            // port 8082 occupe : le programme continue sans console
            System.err.println("Console H2 indisponible : " + e.getMessage());
        }
    }

    // garde la base en vie le temps de la consulter dans le navigateur
    public static void attendreEntreePuisArreter() {
        if (serveur != null) {
            System.out.println();
            System.out.println("Base consultable sur " + ADRESSE + ". Appuyez sur Entrée pour quitter...");
            Scanner clavier = new Scanner(System.in);
            if (clavier.hasNextLine()) {
                clavier.nextLine();
            }
            serveur.stop();
            serveur = null;
        }
    }
}
