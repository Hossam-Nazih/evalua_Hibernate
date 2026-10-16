package ma.projet.test;

import ma.projet.classes.Categorie;
import ma.projet.classes.Client;
import ma.projet.classes.Commande;
import ma.projet.classes.Fournisseur;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.service.CategorieService;
import ma.projet.service.ClientService;
import ma.projet.service.CommandeService;
import ma.projet.service.FournisseurService;
import ma.projet.service.LigneCommandeService;
import ma.projet.service.ProduitService;
import ma.projet.util.ConsoleH2;
import ma.projet.util.HibernateUtil;

import java.time.LocalDate;

/**
 * Exercice 1 : alimente la base puis passe en revue chaque fonctionnalite.
 * La console H2 reste ouverte a la fin pour inspecter les tables.
 */
public class TestGestionStock {

    public static void main(String[] args) {
        ConsoleH2.demarrer("gestion_stock");
        try {
            JeuDeDonnees donnees = JeuDeDonnees.charger();
            ProduitService produitService = new ProduitService();

            entete("Tous les produits (findAll)");
            produitService.findAll().forEach(System.out::println);

            entete("Produits par catégorie");
            produitService.afficherProduitsParCategorie(donnees.stockage);
            System.out.println();
            produitService.afficherProduitsParCategorie(donnees.peripheriques);

            entete("Produits commandés en mars 2013");
            produitService.afficherProduitsCommandesEntre(LocalDate.of(2013, 3, 1), LocalDate.of(2013, 3, 31));

            entete("Détail d'une commande");
            produitService.afficherProduitsCommande(donnees.commandeMars);

            entete("Requête nommée : prix > 100 DH");
            produitService.afficherProduitsPrixSuperieur(100);

            entete("Nouvelles entités : fournisseur et client");
            new FournisseurService().afficherProduitsFournis(donnees.techno);
            System.out.println();
            new ClientService().afficherHistorique(donnees.yassine);

            entete("Mise à jour puis suppression");
            donnees.ab10.setPrixUnitaire(90);
            produitService.update(donnees.ab10);
            System.out.println("Après update : " + produitService.findById(donnees.ab10.getId()));
            LigneCommandeService ligneService = new LigneCommandeService();
            ligneService.delete(ligneService.findById(donnees.ab10.getId(), donnees.commandeMai.getId()));
            System.out.println("Lignes restantes dans la commande " + donnees.commandeMai.getId() + " : "
                    + produitService.findLignesCommande(donnees.commandeMai).size());

            ConsoleH2.attendreEntreePuisArreter();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void entete(String texte) {
        System.out.println();
        System.out.println("----- " + texte + " -----");
    }

    /**
     * Donnees de demonstration, reutilisees par les tests JUnit.
     */
    public static class JeuDeDonnees {
        public Categorie stockage;
        public Categorie peripheriques;
        public Fournisseur techno;
        public Client yassine;
        public Produit es12, zr85, ee85, ab10;
        public Commande commandeMars;
        public Commande commandeMai;

        public static JeuDeDonnees charger() {
            JeuDeDonnees d = new JeuDeDonnees();
            CategorieService categorieService = new CategorieService();
            ProduitService produitService = new ProduitService();
            CommandeService commandeService = new CommandeService();
            LigneCommandeService ligneService = new LigneCommandeService();

            d.stockage = new Categorie("STO", "Stockage");
            d.peripheriques = new Categorie("PER", "Périphériques");
            categorieService.create(d.stockage);
            categorieService.create(d.peripheriques);

            d.techno = new Fournisseur("TechnoDistri", "Casablanca", "0522000000");
            Fournisseur infoPlus = new Fournisseur("InfoPlus", "Rabat", "0537000000");
            FournisseurService fournisseurService = new FournisseurService();
            fournisseurService.create(d.techno);
            fournisseurService.create(infoPlus);

            d.es12 = new Produit("ES12", 120, d.stockage, d.techno);
            d.zr85 = new Produit("ZR85", 100, d.peripheriques, infoPlus);
            d.ee85 = new Produit("EE85", 200, d.stockage, d.techno);
            d.ab10 = new Produit("AB10", 80, d.peripheriques, infoPlus);
            for (Produit p : new Produit[]{d.es12, d.zr85, d.ee85, d.ab10}) {
                produitService.create(p);
            }

            d.yassine = new Client("Yassine Amrani", "y.amrani@mail.ma");
            new ClientService().create(d.yassine);

            d.commandeMars = new Commande(LocalDate.of(2013, 3, 14), d.yassine);
            d.commandeMai = new Commande(LocalDate.of(2013, 5, 2));
            commandeService.create(d.commandeMars);
            commandeService.create(d.commandeMai);
            ligneService.create(new LigneCommandeProduit(d.es12, d.commandeMars, 7));
            ligneService.create(new LigneCommandeProduit(d.zr85, d.commandeMars, 14));
            ligneService.create(new LigneCommandeProduit(d.ee85, d.commandeMars, 5));
            ligneService.create(new LigneCommandeProduit(d.ab10, d.commandeMai, 3));
            return d;
        }
    }
}
