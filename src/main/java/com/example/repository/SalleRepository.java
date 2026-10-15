package com.example.repository;

import com.example.model.Salle;

import java.time.LocalDateTime;
import java.util.List;

/** Contrat d'acces aux salles : aucune autre couche ne manipule directement l'EntityManager. */
public interface SalleRepository {

    // salles qu'aucune reservation n'occupe entre dateDebut et dateFin (requete JPQL)
    List<Salle> findAvailableRooms(LocalDateTime dateDebut, LocalDateTime dateFin);

    // une page de salles filtrees (API Criteria) ; numeroPage commence a 1
    List<Salle> searchRooms(CritereRecherche critere, int numeroPage, int taillePage);

    // nombre total de salles pour les memes filtres, utilise pour calculer le nombre de pages
    long countRooms(CritereRecherche critere);
}
