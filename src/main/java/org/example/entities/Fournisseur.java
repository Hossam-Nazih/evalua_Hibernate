package org.example.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Societe qui approvisionne le magasin : ses champs propres sont dans la table fournisseurs.
 */
@Entity
@Table(name = "fournisseurs")
public class Fournisseur extends User {
    @Column(name = "adresse_siege")
    private String adresseSiege;

    public Fournisseur() {}
    public Fournisseur(String nomComplet, String email, String adresseSiege) {
        super(nomComplet, email);
        this.adresseSiege = adresseSiege;
    }

    public String getAdresseSiege() { return adresseSiege; }
    public void setAdresseSiege(String adresseSiege) { this.adresseSiege = adresseSiege; }

    @Override
    public String toString() {
        return "Fournisseur{id=" + getId() + ", nomComplet='" + getNomComplet() + "', adresseSiege='" + adresseSiege + "'}";
    }
}
