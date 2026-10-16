package ma.projet.service;

import ma.projet.classes.Commande;
import ma.projet.dao.AbstractDao;

// CRUD des bons de commande ; le detail d'une commande est affiche par ProduitService
public class CommandeService extends AbstractDao<Commande> {

    public CommandeService() {
        super(Commande.class);
    }
}
