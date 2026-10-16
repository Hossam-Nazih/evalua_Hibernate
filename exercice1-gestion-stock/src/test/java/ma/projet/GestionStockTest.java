package ma.projet;

import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.service.ClientService;
import ma.projet.service.FournisseurService;
import ma.projet.service.LigneCommandeService;
import ma.projet.service.ProduitService;
import ma.projet.test.TestGestionStock.JeuDeDonnees;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Controle automatique de l'exercice 1, sur les memes donnees que le programme de test.
 */
public class GestionStockTest {
    private static final ProduitService produitService = new ProduitService();
    private static JeuDeDonnees donnees;

    @BeforeClass
    public static void alimenterBase() {
        donnees = JeuDeDonnees.charger();
    }

    @Test
    public void listeParCategorie() {
        assertEquals(Arrays.asList("EE85", "ES12"), codes(produitService.findByCategorie(donnees.stockage)));
    }

    @Test
    public void commandesDeMars2013() {
        List<Produit> mars = produitService.findProduitsCommandesEntre(LocalDate.of(2013, 3, 1), LocalDate.of(2013, 3, 31));
        assertEquals(Arrays.asList("EE85", "ES12", "ZR85"), codes(mars));
    }

    @Test
    public void blocDetailCommande() {
        String bloc = produitService.detailCommande(donnees.commandeMars);
        assertTrue(bloc.startsWith("Commande : " + donnees.commandeMars.getId()));
        assertTrue(bloc.contains("Date : 14 Mars 2013"));
        assertTrue(bloc.contains("ES12        120 DH  7"));
        assertTrue(bloc.contains("ZR85        100 DH  14"));
        assertTrue(bloc.contains("EE85        200 DH  5"));
    }

    @Test
    public void requeteNommeeAuDessusDe100() {
        // 100 DH pile n'est pas "superieur a 100"
        assertEquals(Arrays.asList("ES12", "EE85"), codes(produitService.findPrixSuperieur(100)));
    }

    @Test
    public void ligneParCleComposee() {
        LigneCommandeProduit ligne = new LigneCommandeService().findById(donnees.ee85.getId(), donnees.commandeMars.getId());
        assertNotNull(ligne);
        assertEquals(5, ligne.getQuantiteCommandee());
    }

    @Test
    public void produitsDUnFournisseur() {
        assertEquals(Arrays.asList("EE85", "ES12"), codes(new FournisseurService().findProduitsFournis(donnees.techno)));
    }

    @Test
    public void historiqueEtTotalDUnClient() {
        ClientService clientService = new ClientService();
        assertEquals(1, clientService.findCommandesDuClient(donnees.yassine).size());
        // 7 x 120 + 14 x 100 + 5 x 200
        assertEquals(3240.0, clientService.totalAchats(donnees.yassine), 0.001);
    }

    private static List<String> codes(List<Produit> produits) {
        return produits.stream().map(Produit::getCodeArticle).collect(Collectors.toList());
    }
}
