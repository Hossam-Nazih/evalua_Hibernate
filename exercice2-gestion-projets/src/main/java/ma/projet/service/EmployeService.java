package ma.projet.service;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import java.util.List;

/**
 * Consultations centrees sur un employe : son travail effectue et les projets qu'il pilote.
 */
public class EmployeService extends AbstractDao<Employe> {

    public EmployeService() {
        super(Employe.class);
    }

    // ===== Taches realisees par l'employe =====

    // la tache est chargee dans la meme requete (JOIN FETCH) pour etre lisible hors session
    public List<EmployeTache> findTachesRealisees(Employe employe) {
        return consulter(session -> session.createQuery(
                        "SELECT a FROM EmployeTache a JOIN FETCH a.tache "
                                + "WHERE a.employe.id = :idEmploye ORDER BY a.debutEffectif", EmployeTache.class)
                .setParameter("idEmploye", employe.getId())
                .list());
    }

    public void afficherTachesRealisees(Employe employe) {
        System.out.println("Tâches réalisées par " + employe.getPrenomEmploye() + " " + employe.getNomEmploye() + " :");
        System.out.println(String.format("%-4s%-15s%-20s%s", "Num", "Nom", "Date Début Réelle", "Date Fin Réelle"));
        for (EmployeTache a : findTachesRealisees(employe)) {
            System.out.println(String.format("%-4d%-15s%-20s%s", a.getTache().getId(), a.getTache().getLibelle(),
                    Formats.dateCourte(a.getDebutEffectif()), Formats.dateCourte(a.getFinEffective())));
        }
    }

    // ===== Projets diriges par l'employe =====

    public List<Projet> findProjetsGeres(Employe employe) {
        return consulter(session -> session.createQuery(
                        "SELECT p FROM Projet p WHERE p.chefProjet.id = :idEmploye ORDER BY p.dateLancement", Projet.class)
                .setParameter("idEmploye", employe.getId())
                .list());
    }

    public void afficherProjetsGeres(Employe employe) {
        System.out.println("Projets dirigés par " + employe.getPrenomEmploye() + " " + employe.getNomEmploye() + " :");
        System.out.println(String.format("%-4s%-22s%-14s%s", "Num", "Intitulé", "Lancement", "Clôture"));
        for (Projet p : findProjetsGeres(employe)) {
            System.out.println(String.format("%-4d%-22s%-14s%s", p.getId(), p.getIntitule(),
                    Formats.dateCourte(p.getDateLancement()), Formats.dateCourte(p.getDateCloture())));
        }
    }
}
