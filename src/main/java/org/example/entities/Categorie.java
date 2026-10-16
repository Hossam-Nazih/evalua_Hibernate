package org.example.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Famille de produits (Informatique, Papeterie...). Son intitule est unique.
 */
@Entity
@Table(name = "categories")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intitule", nullable = false, unique = true, length = 100)
    private String intitule;

    // cote inverse : la cle etrangere est portee par Produit.categorie ;
    // retirer un produit de la liste le supprime de la base (orphanRemoval)
    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produit> listeProduits = new ArrayList<>();

    public Categorie() {}
    public Categorie(String intitule) { this.intitule = intitule; }

    // ces deux methodes mettent a jour les deux extremites de l'association en meme temps
    public void ajouterProduit(Produit p) {
        listeProduits.add(p);
        p.setCategorie(this);
    }

    public void retirerProduit(Produit p) {
        listeProduits.remove(p);
        p.setCategorie(null);
    }

    public Long getId() { return id; }
    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public List<Produit> getListeProduits() { return listeProduits; }

    @Override
    public String toString() { return "Categorie{id=" + id + ", intitule='" + intitule + "'}"; }
}
