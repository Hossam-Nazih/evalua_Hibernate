package ma.projet.service;

import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import java.time.LocalDate;
import java.util.List;

/**
 * CRUD des produits et requetes de consultation du stock.
 * Les methodes findXxx renvoient les donnees, les methodes afficherXxx les ecrivent dans la console.
 */
public class ProduitService extends AbstractDao<Produit> {

    public ProduitService() {
        super(Produit.class);
    }

    // ---------- produits d'une categorie ----------

    public List<Produit> findByCategorie(Categorie categorie) {
        return lire(session -> session.createQuery(
                        "SELECT p FROM Produit p WHERE p.categorie.id = :id ORDER BY p.reference", Produit.class)
                .setParameter("id", categorie.getId())
                .list());
    }

    public void afficherProduitsParCategorie(Categorie categorie) {
        System.out.println("Catégorie : " + categorie.getLibelle());
        System.out.println(String.format("%-12s%s", "Référence", "Prix"));
        for (Produit p : findByCategorie(categorie)) {
            System.out.println(String.format("%-12s%s", p.getReference(), Formats.prix(p.getPrix())));
        }
    }

    // ---------- produits commandes entre deux dates ----------

    // DISTINCT : un produit present dans plusieurs commandes de la periode n'est liste qu'une fois
    public List<Produit> findProduitsCommandesEntre(LocalDate debut, LocalDate fin) {
        return lire(session -> session.createQuery(
                        "SELECT DISTINCT p FROM LigneCommandeProduit l JOIN l.produit p JOIN l.commande c "
                                + "WHERE c.dateCommande BETWEEN :debut AND :fin ORDER BY p.reference", Produit.class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .list());
    }

    public void afficherProduitsCommandesEntre(LocalDate debut, LocalDate fin) {
        System.out.println("Produits commandés entre le " + Formats.dateLongue(debut) + " et le " + Formats.dateLongue(fin) + " :");
        System.out.println(String.format("%-12s%s", "Référence", "Prix"));
        for (Produit p : findProduitsCommandesEntre(debut, fin)) {
            System.out.println(String.format("%-12s%s", p.getReference(), Formats.prix(p.getPrix())));
        }
    }

    // ---------- produits d'une commande ----------

    // JOIN FETCH : la reference et le prix du produit sont lisibles apres fermeture de la session
    public List<LigneCommandeProduit> findLignesCommande(Commande commande) {
        return lire(session -> session.createQuery(
                        "SELECT l FROM LigneCommandeProduit l JOIN FETCH l.produit p "
                                + "WHERE l.commande.id = :id ORDER BY p.reference", LigneCommandeProduit.class)
                .setParameter("id", commande.getId())
                .list());
    }

    /**
     * Texte au format demande :
     * Commande : 4     Date : 14 Mars 2013
     * Liste des produits :
     * Référence   Prix    Quantité
     * ES12        120 DH  7
     */
    public String detailCommande(Commande commande) {
        StringBuilder texte = new StringBuilder();
        texte.append(String.format("Commande : %-6dDate : %s%n", commande.getId(), Formats.dateLongue(commande.getDateCommande())));
        texte.append(String.format("Liste des produits :%n"));
        texte.append(String.format("%-12s%-8s%s%n", "Référence", "Prix", "Quantité"));
        for (LigneCommandeProduit ligne : findLignesCommande(commande)) {
            texte.append(String.format("%-12s%-8s%d%n",
                    ligne.getProduit().getReference(), Formats.prix(ligne.getProduit().getPrix()), ligne.getQuantite()));
        }
        return texte.toString();
    }

    public void afficherProduitsCommande(Commande commande) {
        System.out.print(detailCommande(commande));
    }

    // ---------- requete nommee ----------

    public List<Produit> findPrixSuperieur(double prix) {
        return lire(session -> session.createNamedQuery("Produit.prixSuperieur", Produit.class)
                .setParameter("prix", prix)
                .list());
    }

    public void afficherProduitsPrixSuperieur(double prix) {
        System.out.println("Produits dont le prix dépasse " + Formats.prix(prix) + " :");
        for (Produit p : findPrixSuperieur(prix)) {
            System.out.println(String.format("%-12s%s", p.getReference(), Formats.prix(p.getPrix())));
        }
    }
}
