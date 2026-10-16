package ma.projet.service;

import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import java.util.List;

/**
 * Suivi d'un projet : planning, realisation effective et cout global.
 */
public class ProjetService extends AbstractDao<Projet> {
    private static final String LIGNE_REALISATION = "%-4s%-15s%-20s%s%n";

    public ProjetService() {
        super(Projet.class);
    }

    // ===== Planning =====

    public List<Tache> findTachesPlanifiees(Projet projet) {
        return consulter(session -> session.createQuery(
                        "SELECT t FROM Tache t WHERE t.projet.id = :idProjet ORDER BY t.debutPrevu", Tache.class)
                .setParameter("idProjet", projet.getId())
                .list());
    }

    public void afficherTachesPlanifiees(Projet projet) {
        System.out.println("Planning du projet " + projet.getIntitule() + " :");
        System.out.println(String.format("%-4s%-15s%-14s%-14s%s", "Num", "Nom", "Début prévu", "Fin prévue", "Coût"));
        for (Tache t : findTachesPlanifiees(projet)) {
            System.out.println(String.format("%-4d%-15s%-14s%-14s%s", t.getId(), t.getLibelle(),
                    Formats.dateCourte(t.getDebutPrevu()), Formats.dateCourte(t.getFinPrevue()), Formats.prix(t.getCout())));
        }
    }

    // ===== Realisation avec dates effectives =====

    public List<EmployeTache> findTachesRealisees(Projet projet) {
        return consulter(session -> session.createQuery(
                        "SELECT a FROM EmployeTache a JOIN FETCH a.tache t "
                                + "WHERE t.projet.id = :idProjet ORDER BY a.debutEffectif", EmployeTache.class)
                .setParameter("idProjet", projet.getId())
                .list());
    }

    /**
     * Bloc de texte au format de l'enonce :
     * Projet : 4      Nom : Gestion de stock     Date début : 14 Janvier 2013
     * Liste des tâches:
     * Num Nom            Date Début Réelle   Date Fin Réelle
     * 12  Analyse        10/02/2013          20/02/2013
     */
    public String detailTachesRealisees(Projet projet) {
        StringBuilder bloc = new StringBuilder();
        bloc.append(String.format("Projet : %-7dNom : %-22sDate début : %s%n",
                projet.getId(), projet.getIntitule(), Formats.dateLongue(projet.getDateLancement())));
        bloc.append(String.format("Liste des tâches:%n"));
        bloc.append(String.format(LIGNE_REALISATION, "Num", "Nom", "Date Début Réelle", "Date Fin Réelle"));
        for (EmployeTache a : findTachesRealisees(projet)) {
            bloc.append(String.format(LIGNE_REALISATION, a.getTache().getId(), a.getTache().getLibelle(),
                    Formats.dateCourte(a.getDebutEffectif()), Formats.dateCourte(a.getFinEffective())));
        }
        return bloc.toString();
    }

    public void afficherTachesRealisees(Projet projet) {
        System.out.print(detailTachesRealisees(projet));
    }

    // ===== Cout global (objectif du bureau d'etudes) =====

    // SUM est calcule par la base ; un projet sans tache vaut 0
    public double coutGlobal(Projet projet) {
        Double total = consulter(session -> session.createQuery(
                        "SELECT SUM(t.cout) FROM Tache t WHERE t.projet.id = :idProjet", Double.class)
                .setParameter("idProjet", projet.getId())
                .uniqueResult());
        return total == null ? 0 : total;
    }
}
