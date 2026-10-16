package ma.projet.service;

import ma.projet.beans.Mariage;
import ma.projet.dao.AbstractDao;

public class MariageService extends AbstractDao<Mariage> {

    public MariageService() {
        super(Mariage.class);
    }
}
