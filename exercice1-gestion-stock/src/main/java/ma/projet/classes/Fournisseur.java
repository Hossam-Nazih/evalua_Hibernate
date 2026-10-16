package ma.projet.classes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Nouvelle entite : societe qui livre les produits au magasin.
 * Un fournisseur livre plusieurs produits, un produit vient d'un seul fournisseur.
 */
@Entity
@Table(name = "fournisseur")
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "raison_sociale", nullable = false, unique = true, length = 100)
    private String raisonSociale;

    @Column(name = "ville", length = 60)
    private String ville;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @OneToMany(mappedBy = "fournisseur")
    private List<Produit> produitsFournis = new ArrayList<>();

    public Fournisseur() {}

    public Fournisseur(String raisonSociale, String ville, String telephone) {
        this.raisonSociale = raisonSociale;
        this.ville = ville;
        this.telephone = telephone;
    }

    public int getId() { return id; }
    public String getRaisonSociale() { return raisonSociale; }
    public void setRaisonSociale(String raisonSociale) { this.raisonSociale = raisonSociale; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public List<Produit> getProduitsFournis() { return produitsFournis; }

    @Override
    public String toString() {
        return "Fournisseur{id=" + id + ", raisonSociale='" + raisonSociale + "', ville='" + ville + "'}";
    }
}
