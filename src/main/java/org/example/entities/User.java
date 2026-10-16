package org.example.entities;

import javax.persistence.*;

/**
 * Racine de la hierarchie des utilisateurs. Strategie JOINED : colonnes communes dans users,
 * colonnes propres a chaque sous-classe dans clients et fournisseurs (jointure sur l'id).
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    protected User() {}
    protected User(String nom) { this.nom = nom; }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
