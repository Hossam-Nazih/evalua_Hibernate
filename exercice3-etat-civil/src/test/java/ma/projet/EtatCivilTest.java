package ma.projet;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Personne;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.test.TestEtatCivil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Verifie les fonctionnalites de l'exercice 3 avec le jeu de donnees du programme de test.
 */
public class EtatCivilTest {
    private static final HommeService hommeService = new HommeService();
    private static final FemmeService femmeService = new FemmeService();
    private static final LocalDate DEBUT = LocalDate.of(1985, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2005, 12, 31);

    @BeforeClass
    public static void remplir() {
        TestEtatCivil.creerJeuDeDonnees();
    }

    @Test
    public void dixFemmesEtCinqHommes() {
        assertEquals(10, femmeService.findAll().size());
        assertEquals(5, hommeService.findAll().size());
    }

    @Test
    public void femmeLaPlusAgee() {
        assertEquals("BERRADA", femmeService.findPlusAgee().getNom());
    }

    @Test
    public void epousesDUnHommeEntreDeuxDates() {
        assertEquals(Arrays.asList("ALAMI", "RAMI", "ALI", "ALAOUI"), noms(hommeService.findEpousesEntre(homme("SAFI"), DEBUT, FIN)));
        // Leila SBAI (1980) est hors periode
        assertEquals(Arrays.asList("NACIRI", "HAJJI", "CHERKAOUI"), noms(hommeService.findEpousesEntre(homme("TAZI"), DEBUT, FIN)));
    }

    @Test
    public void nombreDEnfantsRequeteNative() {
        // Karima : 0 enfant avec SAFI (1989) + 2 avec BENNANI (1992)
        assertEquals(2, femmeService.nombreEnfantsEntre(femme("ALAMI"), DEBUT, FIN));
        assertEquals(0, femmeService.nombreEnfantsEntre(femme("ALAMI"), LocalDate.of(1989, 1, 1), LocalDate.of(1991, 1, 1)));
    }

    @Test
    public void femmesMarieesAuMoinsDeuxFois() {
        assertEquals(Arrays.asList("ALAMI", "HAJJI"), noms(femmeService.findMarieesAuMoinsDeuxFois()));
    }

    @Test
    public void hommesMariesAQuatreFemmesCriteria() {
        assertEquals(1, hommeService.nombreHommesMariesAQuatreFemmesEntre(DEBUT, FIN));
        assertEquals(Arrays.asList("SAFI"), noms(hommeService.findHommesMariesAQuatreFemmesEntre(DEBUT, FIN)));
        // en elargissant a 1975, TAZI a lui aussi quatre epouses
        assertEquals(2, hommeService.nombreHommesMariesAQuatreFemmesEntre(LocalDate.of(1975, 1, 1), FIN));
    }

    @Test
    public void detailDesMariages() {
        String detail = hommeService.detailMariages(homme("SAFI"));
        String attendu = String.format(
                "Nom : SAFI SAID%n"
                + "Mariages En Cours :%n"
                + "1. Femme : SALIMA RAMI   Date Début : 03/09/1990    Nbr Enfants : 4%n"
                + "2. Femme : AMAL ALI      Date Début : 03/09/1995    Nbr Enfants : 2%n"
                + "3. Femme : WAFA ALAOUI   Date Début : 04/11/2000    Nbr Enfants : 3%n"
                + "%n"
                + "Mariages échoués :%n"
                + "1. Femme : KARIMA ALAMI  Date Début : 03/09/1989    Date Fin : 03/09/1990    Nbr Enfants : 0%n");
        assertEquals(attendu, detail);
    }

    private static Homme homme(String nom) {
        return hommeService.findAll().stream().filter(h -> h.getNom().equals(nom)).findFirst().orElseThrow(AssertionError::new);
    }

    private static Femme femme(String nom) {
        return femmeService.findAll().stream().filter(f -> f.getNom().equals(nom)).findFirst().orElseThrow(AssertionError::new);
    }

    private static List<String> noms(List<? extends Personne> personnes) {
        return personnes.stream().map(Personne::getNom).collect(Collectors.toList());
    }
}
