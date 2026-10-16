package org.example.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Facture emise pour une commande.
 * Relation 1:1 avec Commande : la contrainte unique sur commande_id empeche de facturer deux fois la meme commande.
 * Relation 1:N avec Paiement : une facture peut etre reglee en plusieurs fois.
 */
@Entity
@Table(name = "factures")
@NamedQueries({
    @NamedQuery(name = "Facture.parCommande", query = "SELECT f FROM Facture f WHERE f.commande.id = :idCommande"),
    // COALESCE renvoie 0 lorsque la facture n'a encore aucun paiement
    @NamedQuery(name = "Facture.montantRegle",
                query = "SELECT COALESCE(SUM(p.montantVerse), 0) FROM Paiement p WHERE p.facture.id = :idFacture")
})
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // numero lisible du type FAC-202609-12
    @Column(name = "numero_facture", nullable = false, unique = true, length = 30)
    private String numeroFacture;

    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    // somme des lignes de la commande au moment de la facturation
    @Column(name = "montant_ttc", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTTC;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commande_id", nullable = false, unique = true)
    private Commande commande;

    // les reglements n'ont pas d'existence propre : ils sont enregistres et supprimes avec la facture
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paiement> reglements = new ArrayList<>();

    public Facture() {}
    public Facture(String numeroFacture, LocalDate dateEmission, BigDecimal montantTTC, Commande commande) {
        this.numeroFacture = numeroFacture;
        this.dateEmission = dateEmission;
        this.montantTTC = montantTTC;
        this.commande = commande;
    }

    public void ajouterReglement(Paiement p) {
        reglements.add(p);
        p.setFacture(this);
    }

    public Long getId() { return id; }
    public String getNumeroFacture() { return numeroFacture; }
    public LocalDate getDateEmission() { return dateEmission; }
    public BigDecimal getMontantTTC() { return montantTTC; }
    public Commande getCommande() { return commande; }
    public List<Paiement> getReglements() { return reglements; }

    @Override
    public String toString() {
        return "Facture{id=" + id + ", numeroFacture='" + numeroFacture + "', dateEmission=" + dateEmission
                + ", montantTTC=" + montantTTC + "}";
    }
}
