package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.dao.AbstractDao;

import java.time.LocalDate;
import java.util.List;

public class FemmeService extends AbstractDao<Femme> {

    public FemmeService() {
        super(Femme.class);
    }

    // date de naissance la plus ancienne ; null s'il n'y a aucune femme
    public Femme findPlusAgee() {
        return lire(session -> session.createQuery("FROM Femme f ORDER BY f.dateNaissance", Femme.class)
                .setMaxResults(1)
                .uniqueResult());
    }

    // requete native nommee : la somme SQL peut revenir en Long, BigInteger ou BigDecimal selon la base
    public int nombreEnfantsEntre(Femme femme, LocalDate debut, LocalDate fin) {
        Number total = lire(session -> (Number) session.getNamedNativeQuery("Femme.nombreEnfantsEntre")
                .setParameter("femmeId", femme.getId())
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .uniqueResult());
        return total == null ? 0 : total.intValue();
    }

    // requete JPQL nommee
    public List<Femme> findMarieesAuMoinsDeuxFois() {
        return lire(session -> session.createNamedQuery("Femme.marieesAuMoinsDeuxFois", Femme.class).list());
    }
}
