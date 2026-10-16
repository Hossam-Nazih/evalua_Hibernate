package org.example.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * Acheteur : ses champs propres sont stockes dans la table clients.
 */
@Entity
@Table(name = "clients")
public class Client extends User {
    @Column(name = "date_adhesion")
    private LocalDate dateAdhesion;

    public Client() {}
    public Client(String nomComplet, String email, LocalDate dateAdhesion) {
        super(nomComplet, email);
        this.dateAdhesion = dateAdhesion;
    }

    public LocalDate getDateAdhesion() { return dateAdhesion; }
    public void setDateAdhesion(LocalDate dateAdhesion) { this.dateAdhesion = dateAdhesion; }

    @Override
    public String toString() {
        return "Client{id=" + getId() + ", nomComplet='" + getNomComplet() + "', dateAdhesion=" + dateAdhesion + "}";
    }
}
