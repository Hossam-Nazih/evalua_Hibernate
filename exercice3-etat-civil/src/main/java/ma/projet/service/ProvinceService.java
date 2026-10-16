package ma.projet.service;

import ma.projet.beans.Province;
import ma.projet.dao.AbstractDao;

/**
 * Service de la nouvelle entite Province : effectif des citoyens residents.
 */
public class ProvinceService extends AbstractDao<Province> {

    public ProvinceService() {
        super(Province.class);
    }

    // hommes et femmes sont dans deux tables distinctes (@MappedSuperclass) : deux comptages a additionner
    public long nombreHabitants(Province province) {
        return consulter(session -> {
            Long hommes = session.createQuery("SELECT COUNT(h) FROM Homme h WHERE h.province.id = :idProvince", Long.class)
                    .setParameter("idProvince", province.getId())
                    .uniqueResult();
            Long femmes = session.createQuery("SELECT COUNT(f) FROM Femme f WHERE f.province.id = :idProvince", Long.class)
                    .setParameter("idProvince", province.getId())
                    .uniqueResult();
            return hommes + femmes;
        });
    }
}
