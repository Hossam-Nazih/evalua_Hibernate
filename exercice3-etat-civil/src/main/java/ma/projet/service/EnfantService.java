package ma.projet.service;

import ma.projet.beans.Enfant;
import ma.projet.beans.Femme;
import ma.projet.dao.AbstractDao;

import java.util.List;

/**
 * Service de la nouvelle entite Enfant.
 */
public class EnfantService extends AbstractDao<Enfant> {

    public EnfantService() {
        super(Enfant.class);
    }

    // tous les enfants d'une mere, toutes unions confondues, du plus age au plus jeune
    public List<Enfant> findEnfantsDeLaMere(Femme mere) {
        return consulter(session -> session.createQuery(
                        "SELECT e FROM Enfant e WHERE e.mariage.epouse.id = :idMere ORDER BY e.dateNaissanceEnfant", Enfant.class)
                .setParameter("idMere", mere.getId())
                .list());
    }
}
