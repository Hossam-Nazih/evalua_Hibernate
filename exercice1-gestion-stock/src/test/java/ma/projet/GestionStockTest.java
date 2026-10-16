package ma.projet;

import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.service.CategorieService;
import ma.projet.service.CommandeService;
import ma.projet.service.LigneCommandeService;
import ma.projet.service.ProduitService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Verifie les fonctionnalites de l'exercice 1 sur la base H2 en memoire.
 */
public class GestionStockTest {
    private static final ProduitService produitService = new ProduitService();
    private static Categorie stockage;
    private static Commande commandeMars;
    private static Commande commandeMai;

    @BeforeClass
    public static void remplir() {
        CategorieService categorieService = new CategorieService();
        CommandeService commandeService = new CommandeService();
        LigneCommandeService ligneService = new LigneCommandeService();

        stockage = new Categorie("STO", "Stockage");
        Categorie peripheriques = new Categorie("PER", "Périphériques");
        assertTrue(categorieService.create(stockage));
        assertTrue(categorieService.create(peripheriques));

        Produit es12 = new Produit("ES12", 120, stockage);
        Produit zr85 = new Produit("ZR85", 100, peripheriques);
        Produit ee85 = new Produit("EE85", 200, stockage);
        Produit ab10 = new Produit("AB10", 80, peripheriques);
        for (Produit p : new Produit[]{es12, zr85, ee85, ab10}) {
            assertTrue(produitService.create(p));
        }

        commandeMars = new Commande(LocalDate.of(2013, 3, 14));
        commandeMai = new Commande(LocalDate.of(2013, 5, 2));
        commandeService.create(commandeMars);
        commandeService.create(commandeMai);
        ligneService.create(new LigneCommandeProduit(es12, commandeMars, 7));
        ligneService.create(new LigneCommandeProduit(zr85, commandeMars, 14));
        ligneService.create(new LigneCommandeProduit(ee85, commandeMars, 5));
        ligneService.create(new LigneCommandeProduit(ab10, commandeMai, 3));
    }

    @Test
    public void produitsParCategorie() {
        assertEquals(Arrays.asList("EE85", "ES12"), references(produitService.findByCategorie(stockage)));
    }

    @Test
    public void produitsCommandesEntreDeuxDates() {
        List<Produit> mars = produitService.findProduitsCommandesEntre(LocalDate.of(2013, 3, 1), LocalDate.of(2013, 3, 31));
        assertEquals(Arrays.asList("EE85", "ES12", "ZR85"), references(mars));
    }

    @Test
    public void detailDUneCommande() {
        String detail = produitService.detailCommande(commandeMars);
        assertTrue(detail.startsWith("Commande : " + commandeMars.getId()));
        assertTrue(detail.contains("Date : 14 Mars 2013"));
        assertTrue(detail.contains("ES12        120 DH  7"));
        assertTrue(detail.contains("ZR85        100 DH  14"));
        assertTrue(detail.contains("EE85        200 DH  5"));
    }

    @Test
    public void requeteNommeePrixSuperieurA100() {
        // ZR85 coute exactement 100 DH : il n'est pas "superieur"
        assertEquals(Arrays.asList("ES12", "EE85"), references(produitService.findPrixSuperieur(100)));
    }

    @Test
    public void ligneRetrouveeParSaCleComposee() {
        LigneCommandeProduit ligne = new LigneCommandeService().findById(
                produitService.findPrixSuperieur(150).get(0).getId(), commandeMars.getId());
        assertNotNull(ligne);
        assertEquals(5, ligne.getQuantite());
    }

    private static List<String> references(List<Produit> produits) {
        return produits.stream().map(Produit::getReference).collect(Collectors.toList());
    }
}
