package ma.projet.test;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.service.EmployeService;
import ma.projet.service.EmployeTacheService;
import ma.projet.service.ProjetService;
import ma.projet.service.TacheService;
import ma.projet.util.HibernateUtil;

import java.time.LocalDate;

/**
 * Programme de test de l'exercice 2 : cree employes, projets, taches et realisations,
 * puis execute chaque methode demandee.
 */
public class TestGestionProjets {

    public static void main(String[] args) {
        EmployeService employeService = new EmployeService();
        ProjetService projetService = new ProjetService();
        TacheService tacheService = new TacheService();
        EmployeTacheService employeTacheService = new EmployeTacheService();

        try {
            Employe karim = new Employe("Alaoui", "Karim", "0661000001");
            Employe nadia = new Employe("Idrissi", "Nadia", "0661000002");
            employeService.create(karim);
            employeService.create(nadia);

            Projet stock = new Projet("Gestion de stock", LocalDate.of(2013, 1, 14), LocalDate.of(2013, 6, 30), karim);
            Projet paie = new Projet("Gestion de paie", LocalDate.of(2013, 9, 1), LocalDate.of(2014, 2, 28), karim);
            projetService.create(stock);
            projetService.create(paie);

            Tache analyse = new Tache("Analyse", LocalDate.of(2013, 2, 1), LocalDate.of(2013, 2, 20), 800, stock);
            Tache conception = new Tache("Conception", LocalDate.of(2013, 3, 1), LocalDate.of(2013, 3, 15), 1200, stock);
            Tache developpement = new Tache("Développement", LocalDate.of(2013, 4, 1), LocalDate.of(2013, 4, 30), 3500, stock);
            Tache recueil = new Tache("Recueil", LocalDate.of(2013, 9, 2), LocalDate.of(2013, 9, 20), 600, paie);
            for (Tache t : new Tache[]{analyse, conception, developpement, recueil}) {
                tacheService.create(t);
            }

            employeTacheService.create(new EmployeTache(nadia, analyse, LocalDate.of(2013, 2, 10), LocalDate.of(2013, 2, 20)));
            employeTacheService.create(new EmployeTache(nadia, conception, LocalDate.of(2013, 3, 10), LocalDate.of(2013, 3, 15)));
            employeTacheService.create(new EmployeTache(karim, developpement, LocalDate.of(2013, 4, 10), LocalDate.of(2013, 4, 25)));

            titre("EmployeService : tâches réalisées par un employé");
            employeService.afficherTachesRealisees(nadia);

            titre("EmployeService : projets gérés par un employé");
            employeService.afficherProjetsGeres(karim);

            titre("ProjetService : tâches planifiées d'un projet");
            projetService.afficherTachesPlanifiees(stock);

            titre("ProjetService : tâches réalisées avec les dates réelles");
            projetService.afficherTachesRealisees(stock);

            titre("TacheService : requête nommée, prix > 1000 DH");
            tacheService.afficherTachesPrixSuperieur(1000);

            titre("TacheService : tâches réalisées entre deux dates");
            tacheService.afficherRealiseesEntre(LocalDate.of(2013, 2, 1), LocalDate.of(2013, 3, 31));
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void titre(String texte) {
        System.out.println();
        System.out.println("===== " + texte + " =====");
    }
}
