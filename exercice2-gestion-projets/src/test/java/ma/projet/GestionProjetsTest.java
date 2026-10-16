package ma.projet;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.service.EmployeService;
import ma.projet.service.EmployeTacheService;
import ma.projet.service.ProjetService;
import ma.projet.service.TacheService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Verifie les fonctionnalites de l'exercice 2 sur la base H2 en memoire.
 */
public class GestionProjetsTest {
    private static final EmployeService employeService = new EmployeService();
    private static final ProjetService projetService = new ProjetService();
    private static final TacheService tacheService = new TacheService();
    private static Employe karim;
    private static Employe nadia;
    private static Projet stock;

    @BeforeClass
    public static void remplir() {
        EmployeTacheService employeTacheService = new EmployeTacheService();

        karim = new Employe("Alaoui", "Karim", "0661000001");
        nadia = new Employe("Idrissi", "Nadia", "0661000002");
        assertTrue(employeService.create(karim));
        assertTrue(employeService.create(nadia));

        stock = new Projet("Gestion de stock", LocalDate.of(2013, 1, 14), LocalDate.of(2013, 6, 30), karim);
        Projet paie = new Projet("Gestion de paie", LocalDate.of(2013, 9, 1), LocalDate.of(2014, 2, 28), karim);
        projetService.create(stock);
        projetService.create(paie);

        Tache analyse = new Tache("Analyse", LocalDate.of(2013, 2, 1), LocalDate.of(2013, 2, 20), 800, stock);
        Tache conception = new Tache("Conception", LocalDate.of(2013, 3, 1), LocalDate.of(2013, 3, 15), 1200, stock);
        Tache developpement = new Tache("Développement", LocalDate.of(2013, 4, 1), LocalDate.of(2013, 4, 30), 3500, stock);
        Tache recueil = new Tache("Recueil", LocalDate.of(2013, 9, 2), LocalDate.of(2013, 9, 20), 600, paie);
        for (Tache t : new Tache[]{analyse, conception, developpement, recueil}) {
            assertTrue(tacheService.create(t));
        }

        employeTacheService.create(new EmployeTache(nadia, analyse, LocalDate.of(2013, 2, 10), LocalDate.of(2013, 2, 20)));
        employeTacheService.create(new EmployeTache(nadia, conception, LocalDate.of(2013, 3, 10), LocalDate.of(2013, 3, 15)));
        employeTacheService.create(new EmployeTache(karim, developpement, LocalDate.of(2013, 4, 10), LocalDate.of(2013, 4, 25)));
    }

    @Test
    public void tachesRealiseesParUnEmploye() {
        List<String> noms = employeService.findTachesRealisees(nadia).stream()
                .map(et -> et.getTache().getNom()).collect(Collectors.toList());
        assertEquals(Arrays.asList("Analyse", "Conception"), noms);
    }

    @Test
    public void projetsGeresParUnEmploye() {
        List<String> noms = employeService.findProjetsGeres(karim).stream().map(Projet::getNom).collect(Collectors.toList());
        assertEquals(Arrays.asList("Gestion de stock", "Gestion de paie"), noms);
        assertTrue(employeService.findProjetsGeres(nadia).isEmpty());
    }

    @Test
    public void tachesPlanifieesDUnProjet() {
        assertEquals(Arrays.asList("Analyse", "Conception", "Développement"), noms(projetService.findTachesPlanifiees(stock)));
    }

    @Test
    public void tachesRealiseesAvecDatesReelles() {
        String detail = projetService.detailTachesRealisees(stock);
        assertTrue(detail.startsWith("Projet : " + stock.getId()));
        assertTrue(detail.contains("Nom : Gestion de stock"));
        assertTrue(detail.contains("Date début : 14 Janvier 2013"));
        assertTrue(detail.contains("Analyse        10/02/2013          20/02/2013"));
        assertTrue(detail.contains("Développement  10/04/2013          25/04/2013"));
    }

    @Test
    public void requeteNommeePrixSuperieurA1000() {
        assertEquals(Arrays.asList("Conception", "Développement"), noms(tacheService.findPrixSuperieur(1000)));
    }

    @Test
    public void tachesRealiseesEntreDeuxDates() {
        List<Tache> fevrierMars = tacheService.findRealiseesEntre(LocalDate.of(2013, 2, 1), LocalDate.of(2013, 3, 31));
        assertEquals(Arrays.asList("Analyse", "Conception"), noms(fevrierMars));
    }

    private static List<String> noms(List<Tache> taches) {
        return taches.stream().map(Tache::getNom).collect(Collectors.toList());
    }
}
