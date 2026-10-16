package ma.projet.service;

import ma.projet.classes.Categorie;
import ma.projet.dao.AbstractDao;

// CRUD des categories : tout est herite de AbstractDao
public class CategorieService extends AbstractDao<Categorie> {

    public CategorieService() {
        super(Categorie.class);
    }
}
