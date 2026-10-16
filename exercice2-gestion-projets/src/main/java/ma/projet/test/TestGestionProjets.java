package ma.projet.test;

import ma.projet.classes.Departement;
import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Livrable;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.service.DepartementService;
import ma.projet.service.EmployeService;
import ma.projet.service.EmployeTacheService;
import ma.projet.service.LivrableService;
import ma.projet.service.ProjetService;
import ma.projet.service.TacheService;
import ma.projet.util.ConsoleH2;
import ma.projet.util.Formats;
import ma.projet.util.HibernateUtil;

import java.time.LocalDate;

/**
 * Exercice 2 : alimente la base puis appelle chaque methode demandee par l'enonce.
 * La console H2 reste ouverte a la fin pour inspecter les tables.
 */
public class TestGestionProjets {

    public static void main(String[] args) {
        ConsoleH2.demarrer("gestion_projets");
        try {
            JeuDeDonnees d = JeuDeDonnees.charger();
            EmployeService employeService = new EmployeService();
            ProjetService projetService = new ProjetService();
            TacheService tacheService = new TacheService();

            entete("Tâches réalisées par un employé");
            employeService.afficherTachesRealisees(d.nadia);

            entete("Projets dirigés par un employé");
            employeService.afficherProjetsGeres(d.karim);

            entete("Tâches planifiées d'un projet");
            projetService.afficherTachesPlanifiees(d.stock);

            entete("Tâches réalisées avec les dates réelles");
            projetService.afficherTachesRealisees(d.stock);

            entete("Requête nommée : coût > 1000 DH");
            tacheService.afficherTachesPrixSuperieur(1000);

            entete("Tâches réalisées entre deux dates");
            tacheService.afficherRealiseesEntre(LocalDate.of(2013, 2, 1), LocalDate.of(2013, 3, 31));

            entete("Coût global des projets");
            for (Projet p : projetService.findAll()) {
                System.out.println(String.format("%-22s%s", p.getIntitule(), Formats.prix(projetService.coutGlobal(p))));
            }

            entete("Nouvelles entités : département et livrables");
            new DepartementService().afficherMembres(d.etudes);
            System.out.println();
            LivrableService livrableService = new LivrableService();
            livrableService.afficherEcheancier(d.stock);
            System.out.println("En retard au 01/06/2013 : " + livrableService.findEnRetard(LocalDate.of(2013, 6, 1)));

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
        public Departement etudes;
        public Employe karim;
        public Employe nadia;
        public Projet stock;
        public Projet paie;

        public static JeuDeDonnees charger() {
            JeuDeDonnees d = new JeuDeDonnees();
            EmployeService employeService = new EmployeService();
            ProjetService projetService = new ProjetService();
            TacheService tacheService = new TacheService();
            EmployeTacheService affectationService = new EmployeTacheService();
            LivrableService livrableService = new LivrableService();

            d.etudes = new Departement("ETU", "Études et conception");
            Departement dev = new Departement("DEV", "Développement");
            DepartementService departementService = new DepartementService();
            departementService.create(d.etudes);
            departementService.create(dev);

            d.karim = new Employe("Alaoui", "Karim", "0661000001", dev);
            d.nadia = new Employe("Idrissi", "Nadia", "0661000002", d.etudes);
            Employe sami = new Employe("Bennis", "Sami", "0661000003", d.etudes);
            for (Employe e : new Employe[]{d.karim, d.nadia, sami}) {
                employeService.create(e);
            }

            d.stock = new Projet("Gestion de stock", LocalDate.of(2013, 1, 14), LocalDate.of(2013, 6, 30), d.karim);
            d.paie = new Projet("Gestion de paie", LocalDate.of(2013, 9, 1), LocalDate.of(2014, 2, 28), d.karim);
            projetService.create(d.stock);
            projetService.create(d.paie);

            Tache analyse = new Tache("Analyse", LocalDate.of(2013, 2, 1), LocalDate.of(2013, 2, 20), 800, d.stock);
            Tache conception = new Tache("Conception", LocalDate.of(2013, 3, 1), LocalDate.of(2013, 3, 15), 1200, d.stock);
            Tache developpement = new Tache("Développement", LocalDate.of(2013, 4, 1), LocalDate.of(2013, 4, 30), 3500, d.stock);
            Tache recueil = new Tache("Recueil", LocalDate.of(2013, 9, 2), LocalDate.of(2013, 9, 20), 600, d.paie);
            for (Tache t : new Tache[]{analyse, conception, developpement, recueil}) {
                tacheService.create(t);
            }

            affectationService.create(new EmployeTache(d.nadia, analyse, LocalDate.of(2013, 2, 10), LocalDate.of(2013, 2, 20)));
            affectationService.create(new EmployeTache(d.nadia, conception, LocalDate.of(2013, 3, 10), LocalDate.of(2013, 3, 15)));
            affectationService.create(new EmployeTache(d.karim, developpement, LocalDate.of(2013, 4, 10), LocalDate.of(2013, 4, 25)));

            livrableService.create(new Livrable("Cahier des charges", LocalDate.of(2013, 2, 25), true, d.stock));
            livrableService.create(new Livrable("Maquettes", LocalDate.of(2013, 3, 20), false, d.stock));
            livrableService.create(new Livrable("Version 1.0", LocalDate.of(2013, 6, 30), false, d.stock));
            return d;
        }
    }
}
