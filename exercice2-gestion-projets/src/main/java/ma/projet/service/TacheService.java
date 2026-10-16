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

    // ---------- requete nommee ----------

    public List<Tache> findPrixSuperieur(double prix) {
        return lire(session -> session.createNamedQuery("Tache.prixSuperieur", Tache.class)
                .setParameter("prix", prix)
                .list());
    }

    public void afficherTachesPrixSuperieur(double prix) {
        System.out.println("Tâches dont le prix dépasse " + Formats.prix(prix) + " :");
        for (Tache t : findPrixSuperieur(prix)) {
            System.out.println(String.format("%-4d%-15s%s", t.getId(), t.getNom(), Formats.prix(t.getPrix())));
        }
    }

    // ---------- taches realisees entre deux dates ----------

    // une tache est retenue si sa realisation a commence et s'est terminee dans la periode
    public List<Tache> findRealiseesEntre(LocalDate debut, LocalDate fin) {
        return lire(session -> session.createQuery(
                        "SELECT DISTINCT t FROM EmployeTache et JOIN et.tache t "
                                + "WHERE et.dateDebutReelle >= :debut AND et.dateFinReelle <= :fin ORDER BY t.id", Tache.class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .list());
    }

    public void afficherRealiseesEntre(LocalDate debut, LocalDate fin) {
        System.out.println("Tâches réalisées entre le " + Formats.dateCourte(debut) + " et le " + Formats.dateCourte(fin) + " :");
        for (Tache t : findRealiseesEntre(debut, fin)) {
            System.out.println(String.format("%-4d%s", t.getId(), t.getNom()));
        }
    }
}
