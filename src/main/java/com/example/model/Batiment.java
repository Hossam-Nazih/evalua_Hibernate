package com.example.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Nouvelle entite : batiment qui regroupe plusieurs salles.
 * Permet de filtrer la recherche de salles par batiment.
 */
@Entity
@Table(name = "batiments")
public class Batiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBatiment;

    @Column(nullable = false, unique = true)
    private String nomBatiment;

    private String adresse;

    // cote inverse : la cle etrangere batiment_id est portee par la table salles
    @OneToMany(mappedBy = "batiment")
    private List<Salle> salles = new ArrayList<>();

    protected Batiment() {}

    public Batiment(String nomBatiment, String adresse) {
        this.nomBatiment = nomBatiment;
        this.adresse = adresse;
    }

    // rattache la salle des deux cotes pour que le modele objet reste coherent
    public Salle ajouterSalle(Salle salle) {
        salles.add(salle);
        salle.setBatiment(this);
        return salle;
    }

    public Long getIdBatiment() { return idBatiment; }

    public String getNomBatiment() { return nomBatiment; }
    public void setNomBatiment(String nomBatiment) { this.nomBatiment = nomBatiment; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public List<Salle> getSalles() { return salles; }
}
