package org.example.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Article vendu, rattache a une seule categorie (cote proprietaire de la relation).
 */
@Entity
@Table(name = "produits")
@NamedQueries({
    @NamedQuery(name = "Produit.tous", query = "SELECT p FROM Produit p"),
    @NamedQuery(name = "Produit.parCategorie", query = "SELECT p FROM Produit p WHERE p.categorie.id = :idCategorie"),
    @NamedQuery(name = "Produit.parDesignation",
                query = "SELECT p FROM Produit p WHERE LOWER(p.designation) LIKE LOWER(CONCAT('%', :motCle, '%'))"),
    // la categorie est ramenee dans le meme SELECT pour pouvoir la lire apres fermeture de l'EntityManager
    @NamedQuery(name = "Produit.avecCategorie", query = "SELECT p FROM Produit p JOIN FETCH p.categorie WHERE p.id = :id")
})
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "designation", nullable = false, length = 150)
    private String designation;

    // 10 chiffres dont 2 apres la virgule
    @Column(name = "prix_unitaire", precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    // LocalDate est convertie en colonne DATE sans annotation supplementaire (JPA 2.2)
    @Column(name = "date_ajout")
    private LocalDate dateAjout;

    // chargee seulement a la demande ; colonne categorie_id obligatoire
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    public Produit() {}
    public Produit(String designation, BigDecimal prixUnitaire, Categorie categorie) {
        this.designation = designation;
        this.prixUnitaire = prixUnitaire;
        this.categorie = categorie;
        this.dateAjout = LocalDate.now();
    }

    public Long getId() { return id; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public LocalDate getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDate dateAjout) { this.dateAjout = dateAjout; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    // on n'affiche pas la categorie : sur une entite detachee cela declencherait une LazyInitializationException
    @Override
    public String toString() {
        return "Produit{id=" + id + ", designation='" + designation + "', prixUnitaire=" + prixUnitaire
                + ", dateAjout=" + dateAjout + "}";
    }
}
