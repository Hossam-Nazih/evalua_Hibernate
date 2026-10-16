package org.example.entities;

import javax.persistence.*;

/**
 * Classe mere abstraite des acteurs (clients et fournisseurs).
 * Avec JOINED, la table users garde les champs communs et chaque sous-classe a sa propre table
 * dont la cle primaire est aussi une cle etrangere vers users.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_complet", nullable = false, length = 100)
    private String nomComplet;

    @Column(name = "email", unique = true, length = 120)
    private String email;

    protected User() {}
    protected User(String nomComplet, String email) {
        this.nomComplet = nomComplet;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
