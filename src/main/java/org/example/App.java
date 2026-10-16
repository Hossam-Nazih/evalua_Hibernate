package org.example;

import org.example.entities.Categorie;
import org.example.entities.Client;
import org.example.entities.Commande;
import org.example.entities.Fournisseur;
import org.example.entities.Produit;
import org.example.entities.User;
import org.example.service.CommandeService;
import org.example.service.CrudService;
import org.example.service.ProduitService;
import org.example.service.RapportVentesService;
import org.example.util.HibernateUtil;
import org.example.util.JpaUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

/**
 * Programme de demonstration : remplit la base (si elle est vide) puis execute chaque etape du TP.
 */
public class App {
    private static final CrudService<Categorie, Long> catService = new CrudService<>(Categorie.class);
    private static final ProduitService produitService = new ProduitService();
    private static final CommandeService commandeService = new CommandeService();
    private static final CrudService<User, Long> userService = new CrudService<>(User.class);

    public static void main(String[] args) {
        try {
            if (catService.getAll().isEmpty()) {
                remplirBase();
            } else {
                System.out.println("Base deja remplie : jeu d'essai non reinsere");
            }

            section("Categories (GenericDAO.findAll)");
            catService.getAll().forEach(System.out::println);

            Categorie informatique = catService.getAll().stream()
                    .filter(c -> c.getNom().equals("Informatique")).findFirst().orElseThrow(IllegalStateException::new);
            section("Named query Produit.findByCategorie : " + informatique.getNom());
            produitService.findByCategorie(informatique.getId()).forEach(System.out::println);

            section("Named query Produit.searchByLibelle : 'cl'");
            produitService.searchByLibelle("cl").forEach(System.out::println);

            section("Criteria : prix entre 10 et 100");
            produitService.search(null, new BigDecimal("10"), new BigDecimal("100")).forEach(System.out::println);

            section("Criteria : libelle contenant 'o' et prix <= 50");
            produitService.search("o", null, new BigDecimal("50")).forEach(System.out::println);

            section("Pagination : 3 produits par page");
            long nbPages = (produitService.count() + 2) / 3;
            for (int page = 1; page <= nbPages; page++) {
                System.out.println("-- page " + page + "/" + nbPages);
                produitService.findPage(page, 3).forEach(System.out::println);
            }

            section("Heritage JOINED : requete polymorphe sur User");
            userService.getAll().forEach(System.out::println);

            section("JOIN FETCH : produit et sa categorie, EntityManager deja ferme");
            Produit clavier = produitService.searchByLibelle("Clavier").get(0);
            Produit avecCategorie = produitService.getByIdAvecCategorie(clavier.getId());
            System.out.println(avecCategorie + " -> " + avecCategorie.getCategorie().getNom());

            section("Transaction : un produit a prix negatif annule tout le lot");
            long avant = produitService.count();
            try {
                produitService.createAll(Arrays.asList(
                        new Produit("Tapis de souris", new BigDecimal("9.90"), informatique),
                        new Produit("Webcam defectueuse", new BigDecimal("-15.00"), informatique)));
            } catch (IllegalArgumentException e) {
                System.out.println("Transaction annulee : " + e.getMessage());
            }
            System.out.println("Produits avant : " + avant + ", apres : " + produitService.count());

            section("Session Hibernate native (hibernate.cfg.xml)");
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Long nb = session.createQuery("SELECT COUNT(c) FROM Commande c", Long.class).getSingleResult();
                System.out.println("Nombre de commandes : " + nb);
            }

            YearMonth moisPrecedent = YearMonth.now().minusMonths(1);
            section("Rapport des ventes " + moisPrecedent + " (Criteria : SUM(prix * quantite) par categorie)");
            new RapportVentesService().rapportMoisPrecedent().forEach(System.out::println);
        } finally {
            JpaUtil.shutdown();
            HibernateUtil.shutdown();
        }
    }

    private static void remplirBase() {
        section("Insertion du jeu d'essai");
        Categorie electronique = new Categorie("Électronique");
        Categorie informatique = new Categorie("Informatique");
        Categorie papeterie = new Categorie("Papeterie");

        // cascade ALL sur Categorie.produits : les produits sont enregistres avec leur categorie
        informatique.addProduit(new Produit("Clavier", new BigDecimal("49.99"), null));
        informatique.addProduit(new Produit("Souris", new BigDecimal("19.90"), null));
        informatique.addProduit(new Produit("Ecran 24 pouces", new BigDecimal("159.00"), null));
        electronique.addProduit(new Produit("Casque audio", new BigDecimal("89.00"), null));
        electronique.addProduit(new Produit("Chargeur USB-C", new BigDecimal("25.50"), null));
        papeterie.addProduit(new Produit("Stylo", new BigDecimal("1.20"), null));
        papeterie.addProduit(new Produit("Cahier", new BigDecimal("3.50"), null));

        for (Categorie c : Arrays.asList(electronique, informatique, papeterie)) {
            catService.create(c);
            System.out.println("Catégorie créée avec l'ID : " + c.getId());
        }

        userService.create(new Client("Alami", LocalDate.of(2025, 3, 12)));
        userService.create(new Client("Bennani", LocalDate.of(2026, 1, 5)));
        userService.create(new Fournisseur("TechDistri", "12 rue de l'Industrie, Casablanca"));

        List<Produit> produits = produitService.getAll();
        Produit clavier = trouver(produits, "Clavier");
        Produit souris = trouver(produits, "Souris");
        Produit casque = trouver(produits, "Casque audio");
        Produit stylo = trouver(produits, "Stylo");

        // deux commandes le mois precedent (dans le rapport), une ce mois-ci (hors rapport)
        LocalDate moisPrecedent = YearMonth.now().minusMonths(1).atDay(10);
        Commande c1 = new Commande(moisPrecedent);
        c1.addProduit(clavier, 3);
        c1.addProduit(souris, 5);
        Commande c2 = new Commande(moisPrecedent.plusDays(5));
        c2.addProduit(casque, 2);
        c2.addProduit(stylo, 10);
        Commande c3 = new Commande(LocalDate.now());
        c3.addProduit(clavier, 1);
        for (Commande c : Arrays.asList(c1, c2, c3)) {
            commandeService.create(c);
            System.out.println("Commande créée avec ID = " + c.getId() + " (" + c.getDateCommande() + ")");
        }
    }

    private static Produit trouver(List<Produit> produits, String libelle) {
        return produits.stream().filter(p -> p.getLibelle().equals(libelle)).findFirst().orElseThrow(IllegalStateException::new);
    }

    private static void section(String titre) {
        System.out.println();
        System.out.println("=== " + titre + " ===");
    }
}
