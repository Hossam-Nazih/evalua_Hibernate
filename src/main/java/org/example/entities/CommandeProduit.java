package org.example.entities;

import javax.persistence.*;

/**
 * Ligne de commande : entite associative entre Commande et Produit, portant la quantite.
 */
@Entity
@Table(name = "commande_produit")
public class CommandeProduit {
    @EmbeddedId
    private CommandeProduitPK id;

    // @MapsId : chaque partie de la cle composee est reprise de l'association correspondante
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commandeId")
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produitId")
    private Produit produit;

    @Column(nullable = false)
    private int quantite;

    public CommandeProduit() {}
    public CommandeProduit(Commande commande, Produit produit, int quantite) {
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        // pour une nouvelle commande getId() vaut encore null : @MapsId le renseigne au persist
        this.id = new CommandeProduitPK(commande.getId(), produit.getId());
    }

    public CommandeProduitPK getId() { return id; }
    public Commande getCommande() { return commande; }
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
}
