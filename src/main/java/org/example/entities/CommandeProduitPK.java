package org.example.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Identifiant d'une ligne de commande : le couple (commande, produit).
 * Un meme produit ne peut donc figurer qu'une fois par commande.
 */
@Embeddable
public class CommandeProduitPK implements Serializable {
    @Column(name = "commande_id")
    private Long idCommande;

    @Column(name = "produit_id")
    private Long idProduit;

    public CommandeProduitPK() {}
    public CommandeProduitPK(Long idCommande, Long idProduit) {
        this.idCommande = idCommande;
        this.idProduit = idProduit;
    }

    public Long getIdCommande() { return idCommande; }
    public void setIdCommande(Long idCommande) { this.idCommande = idCommande; }
    public Long getIdProduit() { return idProduit; }
    public void setIdProduit(Long idProduit) { this.idProduit = idProduit; }

    // deux cles sont egales si elles designent la meme commande et le meme produit
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandeProduitPK)) return false;
        CommandeProduitPK autre = (CommandeProduitPK) o;
        return Objects.equals(idCommande, autre.idCommande) && Objects.equals(idProduit, autre.idProduit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCommande, idProduit);
    }
}
