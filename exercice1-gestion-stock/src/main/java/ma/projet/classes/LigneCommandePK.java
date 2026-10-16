package ma.projet.classes;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Cle composee d'une ligne : (produit, commande). Un produit apparait au plus une fois par commande.
 */
@Embeddable
public class LigneCommandePK implements Serializable {
    @Column(name = "produit_id")
    private int produitId;

    @Column(name = "commande_id")
    private int commandeId;

    public LigneCommandePK() {}

    public LigneCommandePK(int produitId, int commandeId) {
        this.produitId = produitId;
        this.commandeId = commandeId;
    }

    public int getProduitId() { return produitId; }
    public int getCommandeId() { return commandeId; }

    // une cle composee se compare par valeur
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LigneCommandePK)) return false;
        LigneCommandePK autre = (LigneCommandePK) o;
        return produitId == autre.produitId && commandeId == autre.commandeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(produitId, commandeId);
    }
}
