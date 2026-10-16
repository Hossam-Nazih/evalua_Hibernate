package ma.projet.classes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Famille de produits (ex. code CAT-PC, libelle "Ordinateurs portables").
 */
@Entity
@Table(name = "categorie")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String libelle;

    // cote inverse : la cle etrangere categorie_id est dans la table produit
    @OneToMany(mappedBy = "categorie")
    private List<Produit> produits = new ArrayList<>();

    public Categorie() {}

    public Categorie(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public List<Produit> getProduits() { return produits; }

    @Override
    public String toString() {
        return "Categorie{id=" + id + ", code='" + code + "', libelle='" + libelle + "'}";
    }
}
