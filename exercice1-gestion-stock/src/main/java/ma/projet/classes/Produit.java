package ma.projet.classes;

import javax.persistence.*;

/**
 * Article en stock. Son code (ES12, ZR85...) est celui imprime sur les bons de commande.
 */
@Entity
@Table(name = "produit")
// requete nommee de l'enonce : articles dont le prix depasse un seuil, du moins cher au plus cher
@NamedQuery(name = "Produit.plusCherQue",
        query = "SELECT p FROM Produit p WHERE p.prixUnitaire > :seuil ORDER BY p.prixUnitaire")
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code_article", nullable = false, unique = true, length = 20)
    private String codeArticle;

    // prix de vente d'une unite, en dirhams
    @Column(name = "prix_unitaire", nullable = false)
    private double prixUnitaire;

    // chargement differe : la categorie n'est lue que si on y accede dans la session
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    // facultatif : un produit peut etre saisi avant de connaitre son fournisseur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    public Produit() {}

    public Produit(String codeArticle, double prixUnitaire, Categorie categorie) {
        this.codeArticle = codeArticle;
        this.prixUnitaire = prixUnitaire;
        this.categorie = categorie;
    }

    public Produit(String codeArticle, double prixUnitaire, Categorie categorie, Fournisseur fournisseur) {
        this(codeArticle, prixUnitaire, categorie);
        this.fournisseur = fournisseur;
    }

    public int getId() { return id; }
    public String getCodeArticle() { return codeArticle; }
    public void setCodeArticle(String codeArticle) { this.codeArticle = codeArticle; }
    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
    public Fournisseur getFournisseur() { return fournisseur; }
    public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }

    @Override
    public String toString() {
        return "Produit{id=" + id + ", codeArticle='" + codeArticle + "', prixUnitaire=" + prixUnitaire + "}";
    }
}
