package ma.projet.service;

import ma.projet.classes.EmployeTache;
import ma.projet.classes.EmployeTachePK;
import ma.projet.dao.AbstractDao;

public class EmployeTacheService extends AbstractDao<EmployeTache> {

    public EmployeTacheService() {
        super(EmployeTache.class);
    }

    // une affectation se retrouve par le couple (employe, tache), pas par un entier
    @Override
    public EmployeTache findById(int id) {
        throw new UnsupportedOperationException("Utiliser findById(employeId, tacheId)");
    }

    public EmployeTache findById(int employeId, int tacheId) {
        return lire(session -> session.get(EmployeTache.class, new EmployeTachePK(employeId, tacheId)));
    }
}
