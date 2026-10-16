package org.example;

import org.example.entities.Categorie;
import org.example.entities.Commande;
import org.example.entities.Facture;
import org.example.entities.ModePaiement;
import org.example.entities.Produit;
import org.example.service.CommandeService;
import org.example.service.CrudService;
import org.example.service.FactureService;
import org.example.service.LigneRapportVentes;
import org.example.service.ProduitService;
import org.example.service.RapportVentesService;
import org.example.util.JpaUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests d'integration : ils utilisent la meme base H2 en memoire que l'application,
 * recreee a chaque execution (JVM de test separee). Chaque test cree ses propres categories.
 */
public class ProduitDaoTest {
    private static CrudService<Categorie, Long> categorieService;
    private static ProduitService produitService;
    private static CommandeService commandeService;
    private static FactureService factureService;

    @BeforeClass
    public static void preparer() {
        categorieService = new CrudService<>(Categorie.class);
        produitService = new ProduitService();
        commandeService = new CommandeService();
        factureService = new FactureService();
    }

    @AfterClass
    public static void liberer() {
        JpaUtil.shutdown();
    }

    @Test
    public void produitEnregistreAvecSaCategorie() {
        Categorie categorie = new Categorie("Papeterie");
        categorieService.create(categorie);
        Produit stylo = new Produit("Stylo", new BigDecimal("1.20"), categorie);
        produitService.create(stylo);
        assertNotNull(stylo.getId());
        // relecture avec JOIN FETCH : la categorie LAZY est deja chargee
        Produit relu = produitService.getByIdAvecCategorie(stylo.getId());
        assertEquals("Papeterie", relu.getCategorie().getIntitule());
    }

    @Test
    public void rechercheCriteriaEtPagination() {
        Categorie categorie = new Categorie("Bureautique");
        categorie.ajouterProduit(new Produit("Agrafeuse", new BigDecimal("12.00"), null));
        categorie.ajouterProduit(new Produit("Classeur", new BigDecimal("4.00"), null));
        categorie.ajouterProduit(new Produit("Lampe de bureau", new BigDecimal("35.00"), null));
        categorieService.create(categorie);

        List<Produit> trouves = produitService.search("a", new BigDecimal("5"), new BigDecimal("40"));
        assertTrue(trouves.stream().anyMatch(p -> p.getDesignation().equals("Agrafeuse")));
        assertTrue(trouves.stream().anyMatch(p -> p.getDesignation().equals("Lampe de bureau")));
        assertTrue(trouves.stream().noneMatch(p -> p.getDesignation().equals("Classeur")));

        assertEquals(3, produitService.findByCategorie(categorie.getId()).size());
        assertEquals(2, produitService.findPage(1, 2).size());
    }

    @Test
    public void lotAnnuleSiUnPrixEstNegatif() {
        Categorie categorie = new Categorie("Jardin");
        categorieService.create(categorie);
        long avant = produitService.count();
        try {
            produitService.createAll(Arrays.asList(
                    new Produit("Rateau", new BigDecimal("15.00"), categorie),
                    new Produit("Pelle", new BigDecimal("-1.00"), categorie)));
            fail("IllegalArgumentException attendue");
        } catch (IllegalArgumentException attendue) {
            // le rollback doit aussi avoir retire le rateau
        }
        assertEquals(avant, produitService.count());
    }

    @Test
    public void rapportMensuelParCategorie() {
        Categorie son = new Categorie("Son");
        son.ajouterProduit(new Produit("Enceinte", new BigDecimal("50.00"), null));
        son.ajouterProduit(new Produit("Micro", new BigDecimal("20.00"), null));
        Categorie video = new Categorie("Video");
        video.ajouterProduit(new Produit("Camera", new BigDecimal("300.00"), null));
        categorieService.create(son);
        categorieService.create(video);

        Produit enceinte = son.getListeProduits().get(0);
        Produit micro = son.getListeProduits().get(1);
        Produit camera = video.getListeProduits().get(0);

        YearMonth mois = YearMonth.of(2020, 3);
        Commande milieuDeMois = new Commande(mois.atDay(15));
        milieuDeMois.ajouterProduit(enceinte, 2);   // 100
        milieuDeMois.ajouterProduit(micro, 3);      //  60
        commandeService.create(milieuDeMois);
        Commande dernierJour = new Commande(mois.atEndOfMonth());
        dernierJour.ajouterProduit(enceinte, 1);    //  50
        commandeService.create(dernierJour);
        Commande moisSuivant = new Commande(mois.plusMonths(1).atDay(1));
        moisSuivant.ajouterProduit(camera, 1);      // hors periode
        commandeService.create(moisSuivant);

        List<LigneRapportVentes> rapport = new RapportVentesService().rapportMensuel(mois);
        assertEquals(0, new BigDecimal("210.00").compareTo(chiffreAffaires(rapport, "Son")));
        assertEquals(0, BigDecimal.ZERO.compareTo(chiffreAffaires(rapport, "Video")));
        assertEquals("Son", rapport.get(0).getIntituleCategorie());
    }

    @Test
    public void factureEtPaiementsPartiels() {
        Categorie categorie = new Categorie("Electromenager");
        categorie.ajouterProduit(new Produit("Bouilloire", new BigDecimal("30.00"), null));
        categorieService.create(categorie);
        Commande commande = new Commande(LocalDate.now());
        commande.ajouterProduit(categorie.getListeProduits().get(0), 4);   // 120
        commandeService.create(commande);

        Facture facture = factureService.facturer(commande.getId());
        assertEquals(0, new BigDecimal("120.00").compareTo(facture.getMontantTTC()));

        factureService.enregistrerPaiement(facture.getId(), new BigDecimal("70.00"), ModePaiement.CARTE);
        assertEquals(0, new BigDecimal("50.00").compareTo(factureService.resteAPayer(facture.getId())));

        // plus que le reste du : refuse
        try {
            factureService.enregistrerPaiement(facture.getId(), new BigDecimal("60.00"), ModePaiement.CHEQUE);
            fail("paiement excedentaire accepte");
        } catch (IllegalArgumentException attendue) {
            // rien n'a ete enregistre
        }
        assertEquals(0, new BigDecimal("50.00").compareTo(factureService.resteAPayer(facture.getId())));

        // une commande ne se facture qu'une fois
        try {
            factureService.facturer(commande.getId());
            fail("double facturation acceptee");
        } catch (IllegalStateException attendue) {
            // ok
        }
    }

    private static BigDecimal chiffreAffaires(List<LigneRapportVentes> rapport, String intitule) {
        return rapport.stream()
                .filter(l -> l.getIntituleCategorie().equals(intitule))
                .findFirst()
                .orElseThrow(AssertionError::new)
                .getChiffreAffaires();
    }
}
