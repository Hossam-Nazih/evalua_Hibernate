package org.example;

import org.example.entities.Categorie;
import org.example.entities.Facture;
import org.example.entities.ModePaiement;
import org.example.entities.Produit;
import org.example.entities.User;
import org.example.service.CrudService;
import org.example.service.FactureService;
import org.example.service.ProduitService;
import org.example.service.RapportVentesService;
import org.example.util.ConsoleH2;
import org.example.util.JeuDeDonnees;
import org.example.util.JpaUtil;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Lancement en mode console : charge les donnees de demonstration, parcourt chaque notion du TP,
 * puis laisse la console H2 ouverte pour consulter les tables.
 */
public class App {
    private static final CrudService<Categorie, Long> categorieService = new CrudService<>(Categorie.class);
    private static final ProduitService produitService = new ProduitService();
    private static final CrudService<User, Long> userService = new CrudService<>(User.class);
    private static final FactureService factureService = new FactureService();

    public static void main(String[] args) {
        ConsoleH2.demarrer();
        try {
            JeuDeDonnees.charger();

            titre("Categories (GenericDAO.findAll)");
            categorieService.getAll().forEach(System.out::println);

            Categorie informatique = categorieService.getAll().stream()
                    .filter(c -> c.getIntitule().equals("Informatique"))
                    .findFirst().orElseThrow(IllegalStateException::new);
            titre("Requete nommee Produit.parCategorie : " + informatique.getIntitule());
            produitService.findByCategorie(informatique.getId()).forEach(System.out::println);

            titre("Requete nommee Produit.parDesignation : 'cl'");
            produitService.searchByDesignation("cl").forEach(System.out::println);

            titre("Criteria : prix unitaire entre 10 et 100");
            produitService.search(null, new BigDecimal("10"), new BigDecimal("100")).forEach(System.out::println);

            titre("Criteria : designation contenant 'o', prix <= 50");
            produitService.search("o", null, new BigDecimal("50")).forEach(System.out::println);

            titre("Pagination : 3 produits par page");
            long nbPages = (produitService.count() + 2) / 3;
            for (int page = 1; page <= nbPages; page++) {
                System.out.println("-- page " + page + "/" + nbPages);
                produitService.findPage(page, 3).forEach(System.out::println);
            }

            titre("Heritage JOINED : lecture polymorphe de User");
            userService.getAll().forEach(System.out::println);

            titre("JOIN FETCH : categorie lisible apres fermeture de l'EntityManager");
            Produit clavier = produitService.searchByDesignation("Clavier").get(0);
            Produit charge = produitService.getByIdAvecCategorie(clavier.getId());
            System.out.println(charge + " -> " + charge.getCategorie().getIntitule());

            titre("Transaction : un prix negatif annule tout le lot");
            long avant = produitService.count();
            try {
                produitService.createAll(Arrays.asList(
                        new Produit("Tapis de souris", new BigDecimal("9.90"), informatique),
                        new Produit("Webcam defectueuse", new BigDecimal("-15.00"), informatique)));
            } catch (IllegalArgumentException e) {
                System.out.println("Transaction annulee : " + e.getMessage());
            }
            System.out.println("Produits avant : " + avant + ", apres : " + produitService.count());

            titre("Factures et paiements");
            for (Facture f : factureService.getAll()) {
                System.out.println(f + " -> reste a payer : " + factureService.resteAPayer(f.getId()));
            }
            Facture premiere = factureService.getAll().get(0);
            try {
                factureService.enregistrerPaiement(premiere.getId(), new BigDecimal("1000.00"), ModePaiement.VIREMENT);
            } catch (IllegalArgumentException e) {
                System.out.println("Paiement refuse : " + e.getMessage());
            }

            YearMonth moisDernier = YearMonth.now().minusMonths(1);
            titre("Rapport des ventes " + moisDernier + " (Criteria : SUM(prix x quantite) par categorie)");
            new RapportVentesService().rapportMoisPrecedent().forEach(System.out::println);

            // la base n'existe qu'en memoire : elle disparait avec la JVM
            System.out.println();
            System.out.println("Console H2 : " + ConsoleH2.URL + "  (JDBC URL : jdbc:h2:mem:gestion_commerciale, utilisateur : sa)");
            System.out.println("Appuyez sur Entree pour quitter...");
            new Scanner(System.in).nextLine();
        } finally {
            ConsoleH2.arreter();
            JpaUtil.shutdown();
        }
    }

    private static void titre(String texte) {
        System.out.println();
        System.out.println("=== " + texte + " ===");
    }
}
