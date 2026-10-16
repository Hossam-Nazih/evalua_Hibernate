package ma.projet.service;

import ma.projet.classes.LigneCommandePK;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.dao.AbstractDao;

public class LigneCommandeService extends AbstractDao<LigneCommandeProduit> {

    public LigneCommandeService() {
        super(LigneCommandeProduit.class);
    }

    // la cle d'une ligne est composee : un seul entier ne suffit pas a la retrouver
    @Override
    public LigneCommandeProduit findById(int id) {
        throw new UnsupportedOperationException("Appeler findById(idProduit, idCommande)");
    }

    public LigneCommandeProduit findById(int idProduit, int idCommande) {
        return consulter(session -> session.get(LigneCommandeProduit.class, new LigneCommandePK(idProduit, idCommande)));
    }
}
