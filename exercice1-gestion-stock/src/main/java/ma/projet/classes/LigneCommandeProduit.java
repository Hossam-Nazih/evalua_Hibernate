package ma.projet.classes;

import javax.persistence.*;

/**
 * Association entre Commande et Produit, avec la quantite commandee.
 */
@Entity
@Table(name = "ligne_commande_produit")
public class LigneCommandeProduit {
    @EmbeddedId
    private LigneCommandePK id;

    // les colonnes de jointure sont deja ecrites par la cle composee : ces deux associations sont en lecture seule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", insertable = false, updatable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", insertable = false, updatable = false)
    private Commande commande;

    @Column(nullable = false)
    private int quantite;

    public LigneCommandeProduit() {}

    // produit et commande doivent deja etre enregistres (leurs id servent a former la cle)
    public LigneCommandeProduit(Produit produit, Commande commande, int quantite) {
        this.id = new LigneCommandePK(produit.getId(), commande.getId());
        this.produit = produit;
        this.commande = commande;
        this.quantite = quantite;
    }

    public LigneCommandePK getId() { return id; }
    public Produit getProduit() { return produit; }
    public Commande getCommande() { return commande; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
}
