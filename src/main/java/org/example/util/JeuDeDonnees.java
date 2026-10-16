package org.example.util;

import org.example.entities.Categorie;
import org.example.entities.Client;
import org.example.entities.Commande;
import org.example.entities.Facture;
import org.example.entities.Fournisseur;
import org.example.entities.ModePaiement;
import org.example.entities.Produit;
import org.example.service.CommandeService;
import org.example.service.CrudService;
import org.example.service.FactureService;
import org.example.service.ProduitService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

/**
 * Donnees de demonstration, utilisees par App et au demarrage de l'application web.
 * La base H2 etant en memoire, elle est vide a chaque lancement.
 */
public class JeuDeDonnees {

    private JeuDeDonnees() {}

    // ne fait rien si des categories existent deja
    public static void charger() {
        CrudService<Categorie, Long> categories = new CrudService<>(Categorie.class);
        if (!categories.getAll().isEmpty()) {
            return;
        }

        Categorie electronique = new Categorie("Électronique");
        Categorie informatique = new Categorie("Informatique");
        Categorie papeterie = new Categorie("Papeterie");

        // grace a la cascade sur listeProduits, enregistrer la categorie enregistre aussi ses produits
        informatique.ajouterProduit(new Produit("Clavier", new BigDecimal("49.99"), null));
        informatique.ajouterProduit(new Produit("Souris", new BigDecimal("19.90"), null));
        informatique.ajouterProduit(new Produit("Ecran 24 pouces", new BigDecimal("159.00"), null));
        electronique.ajouterProduit(new Produit("Casque audio", new BigDecimal("89.00"), null));
        electronique.ajouterProduit(new Produit("Chargeur USB-C", new BigDecimal("25.50"), null));
        papeterie.ajouterProduit(new Produit("Stylo", new BigDecimal("1.20"), null));
        papeterie.ajouterProduit(new Produit("Cahier", new BigDecimal("3.50"), null));
        for (Categorie c : Arrays.asList(electronique, informatique, papeterie)) {
            categories.create(c);
        }

        CrudService<Client, Long> clients = new CrudService<>(Client.class);
        Client alami = new Client("Yassine Alami", "y.alami@mail.ma", LocalDate.of(2025, 3, 12));
        Client bennani = new Client("Salma Bennani", "s.bennani@mail.ma", LocalDate.of(2026, 1, 5));
        clients.create(alami);
        clients.create(bennani);
        new CrudService<>(Fournisseur.class)
                .create(new Fournisseur("TechDistri", "contact@techdistri.ma", "12 rue de l'Industrie, Casablanca"));

        List<Produit> produits = new ProduitService().getAll();
        Produit clavier = trouver(produits, "Clavier");
        Produit souris = trouver(produits, "Souris");
        Produit casque = trouver(produits, "Casque audio");
        Produit stylo = trouver(produits, "Stylo");

        // deux commandes le mois dernier (prises dans le rapport) et une ce mois-ci (ignoree)
        LocalDate moisDernier = YearMonth.now().minusMonths(1).atDay(10);
        Commande c1 = new Commande(moisDernier, alami);
        c1.ajouterProduit(clavier, 3);
        c1.ajouterProduit(souris, 5);
        Commande c2 = new Commande(moisDernier.plusDays(5), bennani);
        c2.ajouterProduit(casque, 2);
        c2.ajouterProduit(stylo, 10);
        Commande c3 = new Commande(LocalDate.now(), alami);
        c3.ajouterProduit(clavier, 1);
        CommandeService commandes = new CommandeService();
        for (Commande c : Arrays.asList(c1, c2, c3)) {
            commandes.create(c);
        }

        // la premiere commande est facturee puis reglee en deux fois
        FactureService factures = new FactureService();
        Facture f1 = factures.facturer(c1.getId());
        factures.enregistrerPaiement(f1.getId(), new BigDecimal("100.00"), ModePaiement.CARTE);
        factures.enregistrerPaiement(f1.getId(), new BigDecimal("50.00"), ModePaiement.ESPECES);
    }

    private static Produit trouver(List<Produit> produits, String designation) {
        return produits.stream()
                .filter(p -> p.getDesignation().equals(designation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Produit absent : " + designation));
    }
}
