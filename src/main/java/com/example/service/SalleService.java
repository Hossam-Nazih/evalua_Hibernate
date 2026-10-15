package com.example.service;

import com.example.model.Salle;
import com.example.repository.CritereRecherche;
import com.example.util.PaginationResult;

import java.time.LocalDateTime;
import java.util.List;

/** Operations metier sur les salles, appelees par l'application (ici App). */
public interface SalleService {

    List<Salle> getAvailableRooms(LocalDateTime dateDebut, LocalDateTime dateFin);

    List<Salle> searchRooms(CritereRecherche critere, int numeroPage, int taillePage);

    long countRooms(CritereRecherche critere);

    // la page demandee et ses informations de navigation en un seul appel
    PaginationResult<Salle> searchRoomsPaginated(CritereRecherche critere, int numeroPage, int taillePage);
}
