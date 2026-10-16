package org.example.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Reglement (total ou partiel) d'une facture.
 */
@Entity
@Table(name = "paiements")
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "montant_verse", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantVerse;

    @Column(name = "date_reglement", nullable = false)
    private LocalDate dateReglement;

    // STRING stocke le nom de la constante ("CARTE") : ajouter une valeur a l'enum ne decale pas les donnees
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 20)
    private ModePaiement modePaiement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    public Paiement() {}
    public Paiement(BigDecimal montantVerse, LocalDate dateReglement, ModePaiement modePaiement) {
        this.montantVerse = montantVerse;
        this.dateReglement = dateReglement;
        this.modePaiement = modePaiement;
    }

    public Long getId() { return id; }
    public BigDecimal getMontantVerse() { return montantVerse; }
    public LocalDate getDateReglement() { return dateReglement; }
    public ModePaiement getModePaiement() { return modePaiement; }
    public Facture getFacture() { return facture; }
    public void setFacture(Facture facture) { this.facture = facture; }

    @Override
    public String toString() {
        return "Paiement{id=" + id + ", montantVerse=" + montantVerse + ", dateReglement=" + dateReglement
                + ", modePaiement=" + modePaiement + "}";
    }
}
