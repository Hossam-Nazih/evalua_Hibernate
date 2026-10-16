package ma.projet.service;

import ma.projet.classes.Client;
import ma.projet.classes.Commande;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import java.util.List;

/**
 * Service de la nouvelle entite Client : historique des commandes et total des achats.
 */
public class ClientService extends AbstractDao<Client> {

    public ClientService() {
        super(Client.class);
    }

    public List<Commande> findCommandesDuClient(Client client) {
        return consulter(session -> session.createQuery(
                        "SELECT c FROM Commande c WHERE c.client.id = :idClient ORDER BY c.dateEmission", Commande.class)
                .setParameter("idClient", client.getId())
                .list());
    }

    // somme de prix x quantite sur toutes les lignes de ses commandes, calculee par la base
    public double totalAchats(Client client) {
        Double total = consulter(session -> session.createQuery(
                        "SELECT SUM(l.produit.prixUnitaire * l.quantiteCommandee) FROM LigneCommandeProduit l "
                                + "WHERE l.commande.client.id = :idClient", Double.class)
                .setParameter("idClient", client.getId())
                .uniqueResult());
        return total == null ? 0 : total;
    }

    public void afficherHistorique(Client client) {
        System.out.println("Client : " + client.getNomClient() + " <" + client.getEmail() + ">");
        for (Commande c : findCommandesDuClient(client)) {
            System.out.println("  Commande " + c.getId() + " du " + Formats.dateLongue(c.getDateEmission()));
        }
        System.out.println("  Total des achats : " + Formats.prix(totalAchats(client)));
    }
}
