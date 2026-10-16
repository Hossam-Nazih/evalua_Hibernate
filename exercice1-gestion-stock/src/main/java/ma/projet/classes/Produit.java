package ma.projet.classes;

import javax.persistence.*;

/**
 * Article du stock, identifie par sa reference (ES12, ZR85...).
 */
@Entity
@Table(name = "produit")
// requete nommee demandee par l'enonce : produits plus chers qu'un prix donne
@NamedQuery(name = "Produit.prixSuperieur", query = "SELECT p FROM Produit p WHERE p.prix > :prix ORDER BY p.prix")
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    // prix unitaire en DH
    @Column(nullable = false)
    private double prix;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    public Produit() {}

    public Produit(String reference, double prix, Categorie categorie) {
        this.reference = reference;
        this.prix = prix;
        this.categorie = categorie;
    }

    public int getId() { return id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    @Override
    public String toString() {
        return "Produit{id=" + id + ", reference='" + reference + "', prix=" + prix + "}";
    }
}
