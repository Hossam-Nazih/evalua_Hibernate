package com.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Occupation d'une salle entre dateDebut (incluse) et dateFin (exclue).
 * Une reservation qui finit a 11h n'empeche donc pas une autre de commencer a 11h.
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    // cote proprietaire : la table reservations porte la colonne salle_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    // objet de la reservation (cours, reunion, examen...)
    private String motif;

    protected Reservation() {}

    public Reservation(Salle salle, LocalDateTime dateDebut, LocalDateTime dateFin, String motif) {
        this.salle = salle;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.motif = motif;
    }

    public Long getIdReservation() { return idReservation; }

    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}
