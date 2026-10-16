package ma.projet.classes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Rayon du magasin qui regroupe des produits de meme nature (stockage, peripheriques...).
 */
@Entity
@Table(name = "categorie")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // code court affiche sur les etiquettes, unique dans le magasin
    @Column(name = "code_categorie", nullable = false, unique = true, length = 20)
    private String codeCategorie;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    // relation inverse : c'est Produit.categorie qui porte la colonne categorie_id
    @OneToMany(mappedBy = "categorie")
    private List<Produit> listeProduits = new ArrayList<>();

    public Categorie() {}

    public Categorie(String codeCategorie, String designation) {
        this.codeCategorie = codeCategorie;
        this.designation = designation;
    }

    public int getId() { return id; }
    public String getCodeCategorie() { return codeCategorie; }
    public void setCodeCategorie(String codeCategorie) { this.codeCategorie = codeCategorie; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public List<Produit> getListeProduits() { return listeProduits; }

    @Override
    public String toString() {
        return "Categorie{id=" + id + ", codeCategorie='" + codeCategorie + "', designation='" + designation + "'}";
    }
}
