package ma.projet.service;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import java.util.List;

public class EmployeService extends AbstractDao<Employe> {

    public EmployeService() {
        super(Employe.class);
    }

    // ---------- taches realisees par un employe ----------

    // la tache est chargee avec l'affectation pour rester lisible apres fermeture de la session
    public List<EmployeTache> findTachesRealisees(Employe employe) {
        return lire(session -> session.createQuery(
                        "SELECT et FROM EmployeTache et JOIN FETCH et.tache t "
                                + "WHERE et.employe.id = :id ORDER BY et.dateDebutReelle", EmployeTache.class)
                .setParameter("id", employe.getId())
                .list());
    }

    public void afficherTachesRealisees(Employe employe) {
        System.out.println("Tâches réalisées par " + employe.getPrenom() + " " + employe.getNom() + " :");
        System.out.println(String.format("%-4s%-15s%-20s%s", "Num", "Nom", "Date Début Réelle", "Date Fin Réelle"));
        for (EmployeTache et : findTachesRealisees(employe)) {
            System.out.println(String.format("%-4d%-15s%-20s%s", et.getTache().getId(), et.getTache().getNom(),
                    Formats.dateCourte(et.getDateDebutReelle()), Formats.dateCourte(et.getDateFinReelle())));
        }
    }

    // ---------- projets geres par un employe ----------

    public List<Projet> findProjetsGeres(Employe employe) {
        return lire(session -> session.createQuery(
                        "SELECT p FROM Projet p WHERE p.chef.id = :id ORDER BY p.dateDebut", Projet.class)
                .setParameter("id", employe.getId())
                .list());
    }

    public void afficherProjetsGeres(Employe employe) {
        System.out.println("Projets gérés par " + employe.getPrenom() + " " + employe.getNom() + " :");
        System.out.println(String.format("%-4s%-22s%-14s%s", "Num", "Nom", "Date début", "Date fin"));
        for (Projet p : findProjetsGeres(employe)) {
            System.out.println(String.format("%-4d%-22s%-14s%s", p.getId(), p.getNom(),
                    Formats.dateCourte(p.getDateDebut()), Formats.dateCourte(p.getDateFin())));
        }
    }
}
