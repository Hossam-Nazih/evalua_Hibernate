package ma.projet.service;

import ma.projet.classes.EmployeTache;
import ma.projet.classes.EmployeTachePK;
import ma.projet.dao.AbstractDao;

public class EmployeTacheService extends AbstractDao<EmployeTache> {

    public EmployeTacheService() {
        super(EmployeTache.class);
    }

    // cle composee : la recherche par un seul entier n'a pas de sens ici
    @Override
    public EmployeTache findById(int id) {
        throw new UnsupportedOperationException("Appeler findById(idEmploye, idTache)");
    }

    public EmployeTache findById(int idEmploye, int idTache) {
        return consulter(session -> session.get(EmployeTache.class, new EmployeTachePK(idEmploye, idTache)));
    }
}
