package ma.projet.service;

import ma.projet.classes.Fournisseur;
import ma.projet.classes.Produit;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import java.util.List;

/**
 * Service de la nouvelle entite Fournisseur.
 */
public class FournisseurService extends AbstractDao<Fournisseur> {

    public FournisseurService() {
        super(Fournisseur.class);
    }

    public List<Produit> findProduitsFournis(Fournisseur fournisseur) {
        return consulter(session -> session.createQuery(
                        "SELECT p FROM Produit p WHERE p.fournisseur.id = :idFournisseur ORDER BY p.codeArticle", Produit.class)
                .setParameter("idFournisseur", fournisseur.getId())
                .list());
    }

    public void afficherProduitsFournis(Fournisseur fournisseur) {
        System.out.println("Produits livrés par " + fournisseur.getRaisonSociale() + " (" + fournisseur.getVille() + ") :");
        for (Produit p : findProduitsFournis(fournisseur)) {
            System.out.println(String.format("%-12s%s", p.getCodeArticle(), Formats.prix(p.getPrixUnitaire())));
        }
    }
}
