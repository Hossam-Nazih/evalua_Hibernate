package ma.projet.service;

import ma.projet.classes.Livrable;
import ma.projet.classes.Projet;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import java.time.LocalDate;
import java.util.List;

/**
 * Service de la nouvelle entite Livrable : echeancier d'un projet et livrables en retard.
 */
public class LivrableService extends AbstractDao<Livrable> {

    public LivrableService() {
        super(Livrable.class);
    }

    public List<Livrable> findLivrablesDuProjet(Projet projet) {
        return consulter(session -> session.createQuery(
                        "SELECT l FROM Livrable l WHERE l.projet.id = :idProjet ORDER BY l.dateRemise", Livrable.class)
                .setParameter("idProjet", projet.getId())
                .list());
    }

    // en retard = echeance depassee a la date donnee et pas encore valide par le client
    public List<Livrable> findEnRetard(LocalDate aLaDate) {
        return consulter(session -> session.createQuery(
                        "SELECT l FROM Livrable l WHERE l.valide = false AND l.dateRemise < :jour ORDER BY l.dateRemise", Livrable.class)
                .setParameter("jour", aLaDate)
                .list());
    }

    public void afficherEcheancier(Projet projet) {
        System.out.println("Livrables du projet " + projet.getIntitule() + " :");
        for (Livrable l : findLivrablesDuProjet(projet)) {
            System.out.println(String.format("  %-26s%-12s%s", l.getTitre(), Formats.dateCourte(l.getDateRemise()),
                    l.isValide() ? "validé" : "en attente"));
        }
    }
}
