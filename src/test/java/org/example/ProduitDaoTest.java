package org.example;

import org.example.entities.Categorie;
import org.example.entities.Commande;
import org.example.entities.Produit;
import org.example.service.CommandeService;
import org.example.service.CrudService;
import org.example.service.LigneRapportVentes;
import org.example.service.ProduitService;
import org.example.service.RapportVentesService;
import org.example.util.JpaUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests sur une base H2 en memoire (proprietes fournies par surefire dans le pom).
 */
public class ProduitDaoTest {
    private static CrudService<Categorie, Long> catService;
    private static ProduitService prodService;
    private static CommandeService commandeService;

    @BeforeClass
    public static void init() {
        catService = new CrudService<>(Categorie.class);
        prodService = new ProduitService();
        commandeService = new CommandeService();
    }

    @AfterClass
    public static void fermer() {
        JpaUtil.shutdown();
    }

    @Test
    public void testCreateProduitAvecCategorie() {
        Categorie cat = new Categorie("Papeterie");
        catService.create(cat);
        Produit p = new Produit("Stylo", new BigDecimal("1.20"), cat);
        prodService.create(p);
        assertNotNull(p.getId());
        // categorie LAZY : on la charge avec JOIN FETCH avant la fermeture de l'EntityManager
        Produit fetched = prodService.getByIdAvecCategorie(p.getId());
        assertEquals("Papeterie", fetched.getCategorie().getNom());
    }

    @Test
    public void testRechercheCriteriaEtPagination() {
        Categorie cat = new Categorie("Bureautique");
        cat.addProduit(new Produit("Agrafeuse", new BigDecimal("12.00"), null));
        cat.addProduit(new Produit("Classeur", new BigDecimal("4.00"), null));
        cat.addProduit(new Produit("Lampe de bureau", new BigDecimal("35.00"), null));
        catService.create(cat);

        List<Produit> resultat = prodService.search("a", new BigDecimal("5"), new BigDecimal("40"));
        assertTrue(resultat.stream().anyMatch(p -> p.getLibelle().equals("Agrafeuse")));
        assertTrue(resultat.stream().anyMatch(p -> p.getLibelle().equals("Lampe de bureau")));
        assertTrue(resultat.stream().noneMatch(p -> p.getLibelle().equals("Classeur")));

        assertEquals(3, prodService.findByCategorie(cat.getId()).size());
        assertEquals(2, prodService.findPage(1, 2).size());
    }

    @Test
    public void testTransactionAnnuleeSiPrixNegatif() {
        Categorie cat = new Categorie("Jardin");
        catService.create(cat);
        long avant = prodService.count();
        try {
            prodService.createAll(Arrays.asList(
                    new Produit("Rateau", new BigDecimal("15.00"), cat),
                    new Produit("Pelle", new BigDecimal("-1.00"), cat)));
            fail("une IllegalArgumentException etait attendue");
        } catch (IllegalArgumentException attendue) {
            // le rateau ne doit pas avoir ete enregistre
        }
        assertEquals(avant, prodService.count());
    }

    @Test
    public void testRapportMensuelParCategorie() {
        Categorie son = new Categorie("Son");
        son.addProduit(new Produit("Enceinte", new BigDecimal("50.00"), null));
        son.addProduit(new Produit("Micro", new BigDecimal("20.00"), null));
        Categorie video = new Categorie("Video");
        video.addProduit(new Produit("Camera", new BigDecimal("300.00"), null));
        catService.create(son);
        catService.create(video);

        Produit enceinte = son.getProduits().get(0);
        Produit micro = son.getProduits().get(1);
        Produit camera = video.getProduits().get(0);

        YearMonth mois = YearMonth.of(2020, 3);
        Commande dansLeMois = new Commande(mois.atDay(15));
        dansLeMois.addProduit(enceinte, 2);   // 100
        dansLeMois.addProduit(micro, 3);      //  60
        commandeService.create(dansLeMois);
        Commande finDuMois = new Commande(mois.atEndOfMonth());
        finDuMois.addProduit(enceinte, 1);    //  50
        commandeService.create(finDuMois);
        Commande horsMois = new Commande(mois.plusMonths(1).atDay(1));
        horsMois.addProduit(camera, 1);       // exclue
        commandeService.create(horsMois);

        List<LigneRapportVentes> rapport = new RapportVentesService().rapportMensuel(mois);
        assertEquals(0, new BigDecimal("210.00").compareTo(chiffreAffaires(rapport, "Son")));
        assertEquals(0, BigDecimal.ZERO.compareTo(chiffreAffaires(rapport, "Video")));
        assertEquals("Son", rapport.get(0).getCategorie());
    }

    private static BigDecimal chiffreAffaires(List<LigneRapportVentes> rapport, String categorie) {
        return rapport.stream().filter(l -> l.getCategorie().equals(categorie))
                .findFirst().orElseThrow(AssertionError::new).getChiffreAffaires();
    }
}
