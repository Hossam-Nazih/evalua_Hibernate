package ma.projet.service;

import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import java.util.List;

public class ProjetService extends AbstractDao<Projet> {

    public ProjetService() {
        super(Projet.class);
    }

    // ---------- taches planifiees ----------

    public List<Tache> findTachesPlanifiees(Projet projet) {
        return lire(session -> session.createQuery(
                        "SELECT t FROM Tache t WHERE t.projet.id = :id ORDER BY t.dateDebut", Tache.class)
                .setParameter("id", projet.getId())
                .list());
    }

    public void afficherTachesPlanifiees(Projet projet) {
        System.out.println("Tâches planifiées du projet " + projet.getNom() + " :");
        System.out.println(String.format("%-4s%-15s%-14s%-14s%s", "Num", "Nom", "Date début", "Date fin", "Prix"));
        for (Tache t : findTachesPlanifiees(projet)) {
            System.out.println(String.format("%-4d%-15s%-14s%-14s%s", t.getId(), t.getNom(),
                    Formats.dateCourte(t.getDateDebut()), Formats.dateCourte(t.getDateFin()), Formats.prix(t.getPrix())));
        }
    }

    // ---------- taches realisees avec les dates reelles ----------

    public List<EmployeTache> findTachesRealisees(Projet projet) {
        return lire(session -> session.createQuery(
                        "SELECT et FROM EmployeTache et JOIN FETCH et.tache t "
                                + "WHERE t.projet.id = :id ORDER BY et.dateDebutReelle", EmployeTache.class)
                .setParameter("id", projet.getId())
                .list());
    }

    /**
     * Texte au format demande :
     * Projet : 4      Nom : Gestion de stock     Date début : 14 Janvier 2013
     * Liste des tâches:
     * Num Nom            Date Début Réelle   Date Fin Réelle
     * 12  Analyse        10/02/2013          20/02/2013
     */
    public String detailTachesRealisees(Projet projet) {
        StringBuilder texte = new StringBuilder();
        texte.append(String.format("Projet : %-7dNom : %-22sDate début : %s%n",
                projet.getId(), projet.getNom(), Formats.dateLongue(projet.getDateDebut())));
        texte.append(String.format("Liste des tâches:%n"));
        texte.append(String.format("%-4s%-15s%-20s%s%n", "Num", "Nom", "Date Début Réelle", "Date Fin Réelle"));
        for (EmployeTache et : findTachesRealisees(projet)) {
            texte.append(String.format("%-4d%-15s%-20s%s%n", et.getTache().getId(), et.getTache().getNom(),
                    Formats.dateCourte(et.getDateDebutReelle()), Formats.dateCourte(et.getDateFinReelle())));
        }
        return texte.toString();
    }

    public void afficherTachesRealisees(Projet projet) {
        System.out.print(detailTachesRealisees(projet));
    }
}
