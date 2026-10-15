package com.example;

import com.example.model.Batiment;
import com.example.model.Equipement;
import com.example.model.Salle;
import com.example.repository.CritereRecherche;
import com.example.service.SalleServiceImpl;
import com.example.util.PaginationResult;
import org.h2.tools.Server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Programme de test : remplit la base puis execute chaque fonctionnalite du TP
 * (disponibilite en JPQL, recherche Criteria, pagination, controles du service).
 */
public class App {

    public static void main(String[] args) throws SQLException {
        // la console H2 tourne dans cette JVM, c'est pour cela qu'elle voit la base en memoire
        Server consoleH2 = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SallePU");
        EntityManager em = emf.createEntityManager();

        // pas de conteneur : on donne nous-memes l'EntityManager au service
        SalleServiceImpl service = new SalleServiceImpl();
        service.setEntityManager(em);

        LocalDateTime dateDebut = LocalDateTime.of(2024, 6, 1, 9, 0);
        LocalDateTime dateFin = LocalDateTime.of(2024, 6, 1, 11, 0);

        try {
            remplirBase(em, dateDebut);

            section("Salles libres le " + dateDebut.toLocalDate() + " de 9h a 11h (JPQL)");
            afficher(service.getAvailableRooms(dateDebut, dateFin));

            section("Criteria : libelle contenant 'salle' et au moins 20 places");
            afficher(service.searchRooms(new CritereRecherche().libelle("salle").placesMin(20), 1, 10));

            section("Criteria : Bloc A, au moins 10 places, libres de 9h a 11h");
            afficher(service.searchRooms(new CritereRecherche()
                    .batiment("Bloc A").placesMin(10).libreEntre(dateDebut, dateFin), 1, 10));

            section("Criteria : salles avec videoprojecteur");
            afficher(service.searchRooms(new CritereRecherche().equipement("Videoprojecteur"), 1, 10));

            section("Pagination : toutes les salles, 3 par page");
            CritereRecherche toutes = new CritereRecherche();
            PaginationResult<Salle> page = service.searchRoomsPaginated(toutes, 1, 3);
            System.out.println(page);
            while (page.aUnePageSuivante()) {
                page = service.searchRoomsPaginated(toutes, page.getPageCourante() + 1, 3);
                System.out.println(page);
            }

            section("Controles du service");
            try {
                service.getAvailableRooms(dateFin, dateDebut);
            } catch (IllegalArgumentException e) {
                System.out.println("Refuse : " + e.getMessage());
            }
            try {
                service.searchRooms(toutes, 0, 3);
            } catch (IllegalArgumentException e) {
                System.out.println("Refuse : " + e.getMessage());
            }

            System.out.println("\nConsole H2 : " + consoleH2.getURL());
            System.out.println("  JDBC URL    : jdbc:h2:mem:test");
            System.out.println("  Utilisateur : sa   Mot de passe : (vide)");
            System.out.println("Appuyez sur Entree pour arreter le programme et la console H2...");
            new Scanner(System.in).nextLine();
        } finally {
            // si une erreur a interrompu une transaction, on l'annule avant de tout fermer
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
            emf.close();
            consoleH2.stop();
        }
    }

    /**
     * Jeu d'essai : 2 batiments, 7 salles, 2 equipements et 4 reservations.
     * A101, B201 et Labo Info chevauchent 9h-11h ; l'amphi est reserve de 11h a 12h et reste libre.
     */
    private static void remplirBase(EntityManager em, LocalDateTime jour9h) {
        em.getTransaction().begin();

        Batiment blocA = new Batiment("Bloc A", "Avenue des Sciences");
        Batiment blocB = new Batiment("Bloc B", "Rue de l'Universite");
        Equipement projecteur = new Equipement("Videoprojecteur");
        Equipement tableau = new Equipement("Tableau interactif");

        Salle a101 = blocA.ajouterSalle(new Salle("Salle A101", 30)).equiperDe(projecteur);
        Salle a102 = blocA.ajouterSalle(new Salle("Salle A102", 15)).equiperDe(tableau);
        Salle labo = blocA.ajouterSalle(new Salle("Labo Info", 24)).equiperDe(projecteur);
        Salle reunion = blocA.ajouterSalle(new Salle("Salle de reunion", 12));
        Salle b201 = blocB.ajouterSalle(new Salle("Salle B201", 50)).equiperDe(projecteur).equiperDe(tableau);
        Salle b202 = blocB.ajouterSalle(new Salle("Salle B202", 8));
        Salle amphi = blocB.ajouterSalle(new Salle("Amphi Turing", 200)).equiperDe(projecteur);

        a101.reserver(jour9h, jour9h.plusHours(2), "Cours JPA");                    // 9h-11h
        b201.reserver(jour9h.minusHours(1), jour9h.plusHours(1), "Examen");         // 8h-10h
        labo.reserver(jour9h.plusMinutes(90), jour9h.plusHours(3), "TP Java");      // 10h30-12h
        amphi.reserver(jour9h.plusHours(2), jour9h.plusHours(3), "Conference");     // 11h-12h

        em.persist(blocA);
        em.persist(blocB);
        // les equipements et les reservations suivent grace aux cascades definies dans Salle
        for (Salle s : new Salle[]{a101, a102, labo, reunion, b201, b202, amphi}) {
            em.persist(s);
        }

        em.getTransaction().commit();
        // contexte vide : les requetes suivantes relisent tout depuis la base
        em.clear();
    }

    private static void section(String titre) {
        System.out.println("\n--- " + titre + " ---");
    }

    private static void afficher(List<Salle> salles) {
        if (salles.isEmpty()) System.out.println("(aucune salle)");
        salles.forEach(s -> System.out.println("- " + s));
    }
}
