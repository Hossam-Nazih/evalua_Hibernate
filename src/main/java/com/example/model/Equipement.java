package com.example.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Nouvelle entite : materiel disponible dans une ou plusieurs salles
 * (videoprojecteur, tableau interactif...). Relation ManyToMany avec Salle.
 */
@Entity
@Table(name = "equipements")
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipement;

    @Column(nullable = false, unique = true)
    private String designation;

    // cote inverse : la table de jointure salle_equipement est decrite dans Salle
    @ManyToMany(mappedBy = "equipements")
    private Set<Salle> salles = new HashSet<>();

    protected Equipement() {}

    public Equipement(String designation) {
        this.designation = designation;
    }

    public Long getIdEquipement() { return idEquipement; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Set<Salle> getSalles() { return salles; }
}
