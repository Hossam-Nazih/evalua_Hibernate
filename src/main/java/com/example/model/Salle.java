package com.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Salle que l'on peut reserver. Elle appartient a un batiment, possede des equipements
 * et porte la liste de ses reservations.
 */
@Entity
@Table(name = "salles")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSalle;

    // deux salles ne peuvent pas porter le meme libelle
    @Column(nullable = false, unique = true)
    private String libelle;

    @Column(nullable = false)
    private int nbPlaces;

    // cote proprietaire : colonne batiment_id dans la table salles
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batiment_id")
    private Batiment batiment;

    // cascade PERSIST : un equipement neuf est enregistre avec la salle, mais jamais supprime avec elle
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "salle_equipement",
            joinColumns = @JoinColumn(name = "salle_id"),
            inverseJoinColumns = @JoinColumn(name = "equipement_id"))
    private Set<Equipement> equipements = new HashSet<>();

    // chargees seulement si on les parcourt (LAZY) ; supprimees avec la salle (cascade ALL)
    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reservation> listeReservations = new HashSet<>();

    protected Salle() {}

    public Salle(String libelle, int nbPlaces) {
        this.libelle = libelle;
        this.nbPlaces = nbPlaces;
    }

    // cree la reservation et l'ajoute a la liste : les deux cotes restent synchronises
    public Reservation reserver(LocalDateTime dateDebut, LocalDateTime dateFin, String motif) {
        Reservation reservation = new Reservation(this, dateDebut, dateFin, motif);
        listeReservations.add(reservation);
        return reservation;
    }

    public Salle equiperDe(Equipement equipement) {
        equipements.add(equipement);
        equipement.getSalles().add(this);
        return this;
    }

    public Long getIdSalle() { return idSalle; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public int getNbPlaces() { return nbPlaces; }
    public void setNbPlaces(int nbPlaces) { this.nbPlaces = nbPlaces; }

    public Batiment getBatiment() { return batiment; }
    public void setBatiment(Batiment batiment) { this.batiment = batiment; }

    public Set<Equipement> getEquipements() { return equipements; }

    public Set<Reservation> getListeReservations() { return listeReservations; }

    @Override
    public String toString() {
        return libelle + " (" + nbPlaces + " places)";
    }
}
