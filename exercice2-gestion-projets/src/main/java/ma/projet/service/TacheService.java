package ma.projet.service;

import ma.projet.classes.Tache;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import java.time.LocalDate;
import java.util.List;

public class TacheService extends AbstractDao<Tache> {

    public TacheService() {
        super(Tache.class);
    }

    // ===== Requete nommee Tache.coutSuperieurA =====

    public List<Tache> findPrixSuperieur(double montant) {
        return consulter(session -> session.createNamedQuery("Tache.coutSuperieurA", Tache.class)
                .setParameter("montant", montant)
                .list());
    }

    public void afficherTachesPrixSuperieur(double montant) {
        System.out.println("Tâches coûtant plus de " + Formats.prix(montant) + " :");
        for (Tache t : findPrixSuperieur(montant)) {
            System.out.println(String.format("%-4d%-15s%s", t.getId(), t.getLibelle(), Formats.prix(t.getCout())));
        }
    }

    // ===== Taches realisees sur une periode =====

    // retenue si le travail effectif a debute ET s'est acheve dans l'intervalle
    public List<Tache> findRealiseesEntre(LocalDate du, LocalDate au) {
        return consulter(session -> session.createQuery(
                        "SELECT DISTINCT t FROM EmployeTache a JOIN a.tache t "
                                + "WHERE a.debutEffectif >= :du AND a.finEffective <= :au ORDER BY t.id", Tache.class)
                .setParameter("du", du)
                .setParameter("au", au)
                .list());
    }

    public void afficherRealiseesEntre(LocalDate du, LocalDate au) {
        System.out.println("Tâches réalisées du " + Formats.dateCourte(du) + " au " + Formats.dateCourte(au) + " :");
        for (Tache t : findRealiseesEntre(du, au)) {
            System.out.println(String.format("%-4d%s", t.getId(), t.getLibelle()));
        }
    }
}
