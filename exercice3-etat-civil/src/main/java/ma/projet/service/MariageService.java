package ma.projet.service;

import ma.projet.beans.Mariage;
import ma.projet.dao.AbstractDao;

// enregistrement et modification des unions ; les recherches sont faites depuis HommeService / FemmeService
public class MariageService extends AbstractDao<Mariage> {

    public MariageService() {
        super(Mariage.class);
    }
}
