package ma.projet.util;

import org.h2.tools.Server;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Console web H2 lancee par les programmes de test, sur http://localhost:8082 en principe.
 * Elle doit tourner dans la meme JVM que l'application pour voir la base en memoire :
 * si 8082 est deja pris (par exemple par un autre exercice encore ouvert), on passe au port suivant
 * plutot que de laisser l'utilisateur sur la console d'un autre programme.
 */
public class ConsoleH2 {
    private static final int PREMIER_PORT = 8082;
    private static final int DERNIER_PORT = 8092;

    private static Server serveur;

    private ConsoleH2() {}

    public static void demarrer(String nomBase) {
        for (int port = PREMIER_PORT; port <= DERNIER_PORT && serveur == null; port++) {
            try {
                serveur = Server.createWebServer("-web", "-webPort", String.valueOf(port)).start();
            } catch (SQLException portOccupe) {
                // on essaie le port suivant
            }
        }
        if (serveur == null) {
            System.err.println("Console H2 indisponible : ports " + PREMIER_PORT + " a " + DERNIER_PORT + " occupes");
            return;
        }
        System.out.println("Console H2 : http://localhost:" + serveur.getPort()
                + "  |  JDBC URL : jdbc:h2:mem:" + nomBase + "  |  utilisateur : sa  |  mot de passe : (vide)");
    }

    // garde la base en vie le temps de la consulter dans le navigateur
    public static void attendreEntreePuisArreter() {
        if (serveur != null) {
            System.out.println();
            System.out.println("Base consultable sur http://localhost:" + serveur.getPort() + ". Appuyez sur Entrée pour quitter...");
            Scanner clavier = new Scanner(System.in);
            if (clavier.hasNextLine()) {
                clavier.nextLine();
            }
            serveur.stop();
            serveur = null;
        }
    }
}
