package com.example.repository;

import java.time.LocalDateTime;

/**
 * Regroupe les filtres de la recherche de salles au lieu d'une longue liste de parametres.
 * Un champ laisse a null n'est pas filtre ; le creneau ne compte que si ses deux bornes sont donnees.
 * Utilisation : new CritereRecherche().placesMin(20).batiment("Bloc A")
 */
public class CritereRecherche {

    private String libelle;
    private Integer placesMin;
    private String batiment;
    private String equipement;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    // texte contenu dans le libelle de la salle (casse ignoree)
    public CritereRecherche libelle(String libelle) { this.libelle = libelle; return this; }

    public CritereRecherche placesMin(Integer placesMin) { this.placesMin = placesMin; return this; }

    // nom exact du batiment
    public CritereRecherche batiment(String batiment) { this.batiment = batiment; return this; }

    // designation exacte d'un equipement que la salle doit posseder
    public CritereRecherche equipement(String equipement) { this.equipement = equipement; return this; }

    // la salle doit etre libre sur tout le creneau
    public CritereRecherche libreEntre(LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        return this;
    }

    public String getLibelle() { return libelle; }
    public Integer getPlacesMin() { return placesMin; }
    public String getBatiment() { return batiment; }
    public String getEquipement() { return equipement; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
}
