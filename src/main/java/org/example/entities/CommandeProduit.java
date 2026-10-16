package org.example.entities;

import javax.persistence.*;

/**
 * Table de jointure enrichie entre Commande et Produit : elle porte en plus la quantite commandee.
 */
@Entity
@Table(name = "commande_produit")
public class CommandeProduit {
    @EmbeddedId
    private CommandeProduitPK id;

    // @MapsId relie chaque moitie de la cle composee a l'association du meme nom
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idCommande")
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProduit")
    private Produit produit;

    @Column(name = "quantite_commandee", nullable = false)
    private int quantiteCommandee;

    public CommandeProduit() {}
    public CommandeProduit(Commande commande, Produit produit, int quantiteCommandee) {
        this.commande = commande;
        this.produit = produit;
        this.quantiteCommandee = quantiteCommandee;
        // l'id de la commande est inconnu tant qu'elle n'est pas inseree ; Hibernate le recopie au persist
        this.id = new CommandeProduitPK(commande.getId(), produit.getId());
    }

    public CommandeProduitPK getId() { return id; }
    public Commande getCommande() { return commande; }
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
    public int getQuantiteCommandee() { return quantiteCommandee; }
    public void setQuantiteCommandee(int quantiteCommandee) { this.quantiteCommandee = quantiteCommandee; }
}
