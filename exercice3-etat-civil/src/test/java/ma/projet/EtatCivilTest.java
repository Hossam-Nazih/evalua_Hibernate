package ma.projet;

import ma.projet.beans.Enfant;
import ma.projet.beans.Personne;
import ma.projet.service.EnfantService;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.service.ProvinceService;
import ma.projet.test.TestEtatCivil.JeuDeDonnees;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Controle automatique de l'exercice 3, sur les memes donnees que le programme de test.
 */
public class EtatCivilTest {
    private static final HommeService hommeService = new HommeService();
    private static final FemmeService femmeService = new FemmeService();
    private static final LocalDate DU = LocalDate.of(1985, 1, 1);
    private static final LocalDate AU = LocalDate.of(2005, 12, 31);
    private static JeuDeDonnees d;

    @BeforeClass
    public static void alimenterBase() {
        d = JeuDeDonnees.charger();
    }

    @Test
    public void effectifsDuJeuDeDonnees() {
        assertEquals(10, femmeService.findAll().size());
        assertEquals(5, hommeService.findAll().size());
    }

    @Test
    public void doyenneDesFemmes() {
        assertEquals("BERRADA", femmeService.findPlusAgee().getNomFamille());
    }

    @Test
    public void epousesSurLaPeriode() {
        assertEquals(Arrays.asList("ALAMI", "RAMI", "ALI", "ALAOUI"), noms(hommeService.findEpousesEntre(d.safi, DU, AU)));
        // l'union avec Leila SBAI (1980) precede la periode
        assertEquals(Arrays.asList("NACIRI", "HAJJI", "CHERKAOUI"), noms(hommeService.findEpousesEntre(d.tazi, DU, AU)));
    }

    @Test
    public void requeteNativeNombreDEnfants() {
        // Karima : 0 enfant avec SAFI (1989) puis 2 avec BENNANI (1992)
        assertEquals(2, femmeService.nombreEnfantsEntre(d.karima, DU, AU));
        assertEquals(0, femmeService.nombreEnfantsEntre(d.karima, LocalDate.of(1989, 1, 1), LocalDate.of(1991, 1, 1)));
    }

    @Test
    public void requeteNommeeDeuxUnionsOuPlus() {
        assertEquals(Arrays.asList("ALAMI", "HAJJI"), noms(femmeService.findMarieesAuMoinsDeuxFois()));
    }

    @Test
    public void criteriaQuatreEpouses() {
        assertEquals(1, hommeService.nombreHommesMariesAQuatreFemmesEntre(DU, AU));
        assertEquals(Arrays.asList("SAFI"), noms(hommeService.findHommesMariesAQuatreFemmesEntre(DU, AU)));
        // en remontant a 1975, l'union TAZI / SBAI entre dans la periode : TAZI compte aussi
        assertEquals(2, hommeService.nombreHommesMariesAQuatreFemmesEntre(LocalDate.of(1975, 1, 1), AU));
    }

    @Test
    public void blocDesMariagesConformeALEnonce() {
        String attendu = String.format(
                "Nom : SAFI SAID%n"
                + "Mariages En Cours :%n"
                + "1. Femme : SALIMA RAMI   Date Début : 03/09/1990    Nbr Enfants : 4%n"
                + "2. Femme : AMAL ALI      Date Début : 03/09/1995    Nbr Enfants : 2%n"
                + "3. Femme : WAFA ALAOUI   Date Début : 04/11/2000    Nbr Enfants : 3%n"
                + "%n"
                + "Mariages échoués :%n"
                + "1. Femme : KARIMA ALAMI  Date Début : 03/09/1989    Date Fin : 03/09/1990    Nbr Enfants : 0%n");
        assertEquals(attendu, hommeService.detailMariages(d.safi));
    }

    @Test
    public void habitantsDUneProvince() {
        // Rabat : SAFI + RAMI, ALI, ALAOUI
        assertEquals(4, new ProvinceService().nombreHabitants(d.rabat));
    }

    @Test
    public void enfantsDUneMere() {
        List<String> prenoms = new EnfantService().findEnfantsDeLaMere(d.salima).stream()
                .map(Enfant::getPrenomEnfant).collect(Collectors.toList());
        assertEquals(Arrays.asList("Yassine", "Sara", "Adam", "Lina"), prenoms);
    }

    private static List<String> noms(List<? extends Personne> personnes) {
        return personnes.stream().map(Personne::getNomFamille).collect(Collectors.toList());
    }
}
