package ma.projet.service;

import ma.projet.classes.Commande;
import ma.projet.dao.AbstractDao;

public class CommandeService extends AbstractDao<Commande> {

    public CommandeService() {
        super(Commande.class);
    }
}
