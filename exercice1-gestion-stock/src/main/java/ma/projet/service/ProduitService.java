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
 * Consultations du stock demandees par l'enonce.
 * Convention : findXxx renvoie les objets, afficherXxx imprime le resultat dans la console.
 */
public class ProduitService extends AbstractDao<Produit> {
    private static final String COLONNES = "%-12s%s";

    public ProduitService() {
        super(Produit.class);
    }

    // ===== 1. Produits d'une categorie =====

    public List<Produit> findByCategorie(Categorie categorie) {
        return consulter(session -> session.createQuery(
                        "SELECT p FROM Produit p WHERE p.categorie.id = :idCategorie ORDER BY p.codeArticle", Produit.class)
                .setParameter("idCategorie", categorie.getId())
                .list());
    }

    public void afficherProduitsParCategorie(Categorie categorie) {
        System.out.println("Catégorie : " + categorie.getDesignation());
        System.out.println(String.format(COLONNES, "Référence", "Prix"));
        for (Produit p : findByCategorie(categorie)) {
            System.out.println(String.format(COLONNES, p.getCodeArticle(), Formats.prix(p.getPrixUnitaire())));
        }
    }

    // ===== 2. Produits commandes sur une periode =====

    // un article commande plusieurs fois dans la periode ne doit apparaitre qu'une fois : DISTINCT
    public List<Produit> findProduitsCommandesEntre(LocalDate du, LocalDate au) {
        return consulter(session -> session.createQuery(
                        "SELECT DISTINCT p FROM LigneCommandeProduit l JOIN l.produit p JOIN l.commande c "
                                + "WHERE c.dateEmission BETWEEN :du AND :au ORDER BY p.codeArticle", Produit.class)
                .setParameter("du", du)
                .setParameter("au", au)
                .list());
    }

    public void afficherProduitsCommandesEntre(LocalDate du, LocalDate au) {
        System.out.println("Produits commandés du " + Formats.dateLongue(du) + " au " + Formats.dateLongue(au) + " :");
        System.out.println(String.format(COLONNES, "Référence", "Prix"));
        for (Produit p : findProduitsCommandesEntre(du, au)) {
            System.out.println(String.format(COLONNES, p.getCodeArticle(), Formats.prix(p.getPrixUnitaire())));
        }
    }

    // ===== 3. Contenu d'une commande =====

    // JOIN FETCH ramene le produit avec la ligne : on peut lire son code apres la fermeture de la session
    public List<LigneCommandeProduit> findLignesCommande(Commande commande) {
        return consulter(session -> session.createQuery(
                        "SELECT l FROM LigneCommandeProduit l JOIN FETCH l.produit p "
                                + "WHERE l.commande.id = :idCommande ORDER BY p.codeArticle", LigneCommandeProduit.class)
                .setParameter("idCommande", commande.getId())
                .list());
    }

    /**
     * Construit le bloc de texte attendu :
     * Commande : 4     Date : 14 Mars 2013
     * Liste des produits :
     * Référence   Prix    Quantité
     * ES12        120 DH  7
     */
    public String detailCommande(Commande commande) {
        StringBuilder bloc = new StringBuilder();
        bloc.append(String.format("Commande : %-6dDate : %s%n", commande.getId(), Formats.dateLongue(commande.getDateEmission())));
        bloc.append(String.format("Liste des produits :%n"));
        bloc.append(String.format("%-12s%-8s%s%n", "Référence", "Prix", "Quantité"));
        for (LigneCommandeProduit ligne : findLignesCommande(commande)) {
            Produit p = ligne.getProduit();
            bloc.append(String.format("%-12s%-8s%d%n", p.getCodeArticle(), Formats.prix(p.getPrixUnitaire()), ligne.getQuantiteCommandee()));
        }
        return bloc.toString();
    }

    public void afficherProduitsCommande(Commande commande) {
        System.out.print(detailCommande(commande));
    }

    // ===== 4. Requete nommee Produit.plusCherQue =====

    public List<Produit> findPrixSuperieur(double seuil) {
        return consulter(session -> session.createNamedQuery("Produit.plusCherQue", Produit.class)
                .setParameter("seuil", seuil)
                .list());
    }

    public void afficherProduitsPrixSuperieur(double seuil) {
        System.out.println("Produits à plus de " + Formats.prix(seuil) + " :");
        for (Produit p : findPrixSuperieur(seuil)) {
            System.out.println(String.format(COLONNES, p.getCodeArticle(), Formats.prix(p.getPrixUnitaire())));
        }
    }
}
