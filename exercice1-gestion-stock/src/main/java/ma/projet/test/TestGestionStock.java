package ma.projet.test;

import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.service.CategorieService;
import ma.projet.service.CommandeService;
import ma.projet.service.LigneCommandeService;
import ma.projet.service.ProduitService;
import ma.projet.util.HibernateUtil;

import java.time.LocalDate;

/**
 * Programme de test de l'exercice 1 : remplit le stock puis appelle chaque fonctionnalite.
 */
public class TestGestionStock {

    public static void main(String[] args) {
        CategorieService categorieService = new CategorieService();
        ProduitService produitService = new ProduitService();
        CommandeService commandeService = new CommandeService();
        LigneCommandeService ligneService = new LigneCommandeService();

        try {
            Categorie stockage = new Categorie("STO", "Stockage");
            Categorie peripheriques = new Categorie("PER", "Périphériques");
            categorieService.create(stockage);
            categorieService.create(peripheriques);

            Produit es12 = new Produit("ES12", 120, stockage);
            Produit zr85 = new Produit("ZR85", 100, peripheriques);
            Produit ee85 = new Produit("EE85", 200, stockage);
            Produit ab10 = new Produit("AB10", 80, peripheriques);
            for (Produit p : new Produit[]{es12, zr85, ee85, ab10}) {
                produitService.create(p);
            }

            Commande c1 = new Commande(LocalDate.of(2013, 3, 14));
            Commande c2 = new Commande(LocalDate.of(2013, 5, 2));
            commandeService.create(c1);
            commandeService.create(c2);
            ligneService.create(new LigneCommandeProduit(es12, c1, 7));
            ligneService.create(new LigneCommandeProduit(zr85, c1, 14));
            ligneService.create(new LigneCommandeProduit(ee85, c1, 5));
            ligneService.create(new LigneCommandeProduit(ab10, c2, 3));

            titre("CRUD : liste des produits");
            produitService.findAll().forEach(System.out::println);

            titre("Produits par catégorie");
            produitService.afficherProduitsParCategorie(stockage);
            System.out.println();
            produitService.afficherProduitsParCategorie(peripheriques);

            titre("Produits commandés entre deux dates");
            produitService.afficherProduitsCommandesEntre(LocalDate.of(2013, 3, 1), LocalDate.of(2013, 3, 31));

            titre("Produits d'une commande");
            produitService.afficherProduitsCommande(c1);

            titre("Requête nommée : prix > 100 DH");
            produitService.afficherProduitsPrixSuperieur(100);

            titre("Modification puis suppression");
            ab10.setPrix(90);
            produitService.update(ab10);
            System.out.println("Après update : " + produitService.findById(ab10.getId()));
            LigneCommandeProduit ligne = ligneService.findById(ab10.getId(), c2.getId());
            ligneService.delete(ligne);
            System.out.println("Lignes de la commande " + c2.getId() + " après suppression : "
                    + produitService.findLignesCommande(c2).size());
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void titre(String texte) {
        System.out.println();
        System.out.println("===== " + texte + " =====");
    }
}
