package ma.projet.classes;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Identifiant d'une ligne de commande, forme des deux cles etrangeres.
 * Consequence : un meme article ne peut figurer que sur une ligne par commande.
 */
@Embeddable
public class LigneCommandePK implements Serializable {
    @Column(name = "produit_id")
    private int idProduit;

    @Column(name = "commande_id")
    private int idCommande;

    public LigneCommandePK() {}

    public LigneCommandePK(int idProduit, int idCommande) {
        this.idProduit = idProduit;
        this.idCommande = idCommande;
    }

    public int getIdProduit() { return idProduit; }
    public int getIdCommande() { return idCommande; }

    // Hibernate identifie une ligne par la valeur de ces deux champs
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LigneCommandePK)) return false;
        LigneCommandePK cle = (LigneCommandePK) o;
        return idProduit == cle.idProduit && idCommande == cle.idCommande;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduit, idCommande);
    }
}
