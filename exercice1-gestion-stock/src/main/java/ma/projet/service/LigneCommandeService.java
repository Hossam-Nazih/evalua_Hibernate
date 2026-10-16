package ma.projet.service;

import ma.projet.classes.LigneCommandePK;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.dao.AbstractDao;

public class LigneCommandeService extends AbstractDao<LigneCommandeProduit> {

    public LigneCommandeService() {
        super(LigneCommandeProduit.class);
    }

    // une ligne n'a pas d'identifiant entier : elle se retrouve par le couple (produit, commande)
    @Override
    public LigneCommandeProduit findById(int id) {
        throw new UnsupportedOperationException("Utiliser findById(produitId, commandeId)");
    }

    public LigneCommandeProduit findById(int produitId, int commandeId) {
        return lire(session -> session.get(LigneCommandeProduit.class, new LigneCommandePK(produitId, commandeId)));
    }
}
