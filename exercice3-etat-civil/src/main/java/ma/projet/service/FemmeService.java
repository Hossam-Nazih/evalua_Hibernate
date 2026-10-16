package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.dao.AbstractDao;

import java.time.LocalDate;
import java.util.List;

public class FemmeService extends AbstractDao<Femme> {

    public FemmeService() {
        super(Femme.class);
    }

    // la plus agee = la date de naissance la plus ancienne ; null si la table est vide
    public Femme findPlusAgee() {
        return consulter(session -> session.createQuery("FROM Femme f ORDER BY f.dateDeNaissance", Femme.class)
                .setMaxResults(1)
                .uniqueResult());
    }

    // requete native nommee ; selon la base, SUM renvoie Long, BigInteger ou BigDecimal, d'ou le passage par Number
    public int nombreEnfantsEntre(Femme femme, LocalDate du, LocalDate au) {
        Number total = consulter(session -> (Number) session.getNamedNativeQuery("Femme.enfantsEntreDeuxDates")
                .setParameter("idFemme", femme.getId())
                .setParameter("du", du)
                .setParameter("au", au)
                .uniqueResult());
        return total == null ? 0 : total.intValue();
    }

    // requete JPQL nommee
    public List<Femme> findMarieesAuMoinsDeuxFois() {
        return consulter(session -> session.createNamedQuery("Femme.marieesDeuxFoisOuPlus", Femme.class).list());
    }
}
