package ma.projet.classes;

import javax.persistence.*;

/**
 * Ligne d'un bon de commande : quel produit, dans quelle commande, en quelle quantite.
 */
@Entity
@Table(name = "ligne_commande_produit")
public class LigneCommandeProduit {
    @EmbeddedId
    private LigneCommandePK id;

    // produit_id et commande_id sont ecrits via la cle composee ;
    // ces associations servent seulement a naviguer (insertable/updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", insertable = false, updatable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", insertable = false, updatable = false)
    private Commande commande;

    @Column(name = "quantite_commandee", nullable = false)
    private int quantiteCommandee;

    public LigneCommandeProduit() {}

    // le produit et la commande doivent avoir ete enregistres avant : leurs id composent la cle
    public LigneCommandeProduit(Produit produit, Commande commande, int quantiteCommandee) {
        this.id = new LigneCommandePK(produit.getId(), commande.getId());
        this.produit = produit;
        this.commande = commande;
        this.quantiteCommandee = quantiteCommandee;
    }

    public LigneCommandePK getId() { return id; }
    public Produit getProduit() { return produit; }
    public Commande getCommande() { return commande; }
    public int getQuantiteCommandee() { return quantiteCommandee; }
    public void setQuantiteCommandee(int quantiteCommandee) { this.quantiteCommandee = quantiteCommandee; }
}
