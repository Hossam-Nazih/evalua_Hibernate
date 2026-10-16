package org.example.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produit> produits = new ArrayList<>();

    // Constructeurs
    public Categorie() {}
    public Categorie(String nom) { this.nom = nom; }

    // garde les deux cotes de la relation coherents
    public void addProduit(Produit p) {
        produits.add(p);
        p.setCategorie(this);
    }

    public void removeProduit(Produit p) {
        produits.remove(p);
        p.setCategorie(null);
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public List<Produit> getProduits() { return produits; }

    @Override
    public String toString() { return "Categorie{id=" + id + ", nom='" + nom + "'}"; }
}
