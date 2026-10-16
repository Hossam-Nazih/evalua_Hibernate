package ma.projet;

import ma.projet.classes.Employe;
import ma.projet.classes.Livrable;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.service.DepartementService;
import ma.projet.service.EmployeService;
import ma.projet.service.LivrableService;
import ma.projet.service.ProjetService;
import ma.projet.service.TacheService;
import ma.projet.test.TestGestionProjets.JeuDeDonnees;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Controle automatique de l'exercice 2, sur les memes donnees que le programme de test.
 */
public class GestionProjetsTest {
    private static final EmployeService employeService = new EmployeService();
    private static final ProjetService projetService = new ProjetService();
    private static final TacheService tacheService = new TacheService();
    private static JeuDeDonnees d;

    @BeforeClass
    public static void alimenterBase() {
        d = JeuDeDonnees.charger();
    }

    @Test
    public void travailDUnEmploye() {
        List<String> taches = employeService.findTachesRealisees(d.nadia).stream()
                .map(a -> a.getTache().getLibelle()).collect(Collectors.toList());
        assertEquals(Arrays.asList("Analyse", "Conception"), taches);
    }

    @Test
    public void projetsDiriges() {
        List<String> projets = employeService.findProjetsGeres(d.karim).stream().map(Projet::getIntitule).collect(Collectors.toList());
        assertEquals(Arrays.asList("Gestion de stock", "Gestion de paie"), projets);
        assertTrue(employeService.findProjetsGeres(d.nadia).isEmpty());
    }

    @Test
    public void planningDUnProjet() {
        assertEquals(Arrays.asList("Analyse", "Conception", "Développement"), libelles(projetService.findTachesPlanifiees(d.stock)));
    }

    @Test
    public void realisationAuFormatDeLEnonce() {
        String bloc = projetService.detailTachesRealisees(d.stock);
        assertTrue(bloc.startsWith("Projet : " + d.stock.getId()));
        assertTrue(bloc.contains("Nom : Gestion de stock"));
        assertTrue(bloc.contains("Date début : 14 Janvier 2013"));
        assertTrue(bloc.contains("Analyse        10/02/2013          20/02/2013"));
        assertTrue(bloc.contains("Développement  10/04/2013          25/04/2013"));
    }

    @Test
    public void requeteNommeeAuDessusDe1000() {
        assertEquals(Arrays.asList("Conception", "Développement"), libelles(tacheService.findPrixSuperieur(1000)));
    }

    @Test
    public void realiseesSurUnePeriode() {
        assertEquals(Arrays.asList("Analyse", "Conception"),
                libelles(tacheService.findRealiseesEntre(LocalDate.of(2013, 2, 1), LocalDate.of(2013, 3, 31))));
    }

    @Test
    public void coutGlobalDUnProjet() {
        assertEquals(5500.0, projetService.coutGlobal(d.stock), 0.001);
        assertEquals(600.0, projetService.coutGlobal(d.paie), 0.001);
    }

    @Test
    public void membresDUnDepartement() {
        List<String> noms = new DepartementService().findMembres(d.etudes).stream()
                .map(Employe::getNomEmploye).collect(Collectors.toList());
        assertEquals(Arrays.asList("Bennis", "Idrissi"), noms);
    }

    @Test
    public void livrablesEtRetards() {
        LivrableService livrableService = new LivrableService();
        assertEquals(3, livrableService.findLivrablesDuProjet(d.stock).size());
        // au 1er juin, seules les maquettes (20/03, non validees) sont en retard
        List<String> enRetard = livrableService.findEnRetard(LocalDate.of(2013, 6, 1)).stream()
                .map(Livrable::getTitre).collect(Collectors.toList());
        assertEquals(Arrays.asList("Maquettes"), enRetard);
    }

    private static List<String> libelles(List<Tache> taches) {
        return taches.stream().map(Tache::getLibelle).collect(Collectors.toList());
    }
}
