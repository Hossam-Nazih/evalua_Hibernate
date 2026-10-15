package com.example.service;

import com.example.model.Salle;
import com.example.repository.CritereRecherche;
import com.example.repository.SalleRepository;
import com.example.repository.SalleRepositoryImpl;
import com.example.util.PaginationResult;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Valide les demandes avant de les transmettre au repository.
 * Dans un serveur Java EE la classe serait @Stateless et le repository @Inject ;
 * ici on le branche a la main (constructeur ou setEntityManager).
 */
public class SalleServiceImpl implements SalleService {

    private SalleRepository salleRepository;

    public SalleServiceImpl() {}

    public SalleServiceImpl(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }

    // utilise par App pour fournir l'EntityManager sans conteneur
    public void setEntityManager(EntityManager entityManager) {
        this.salleRepository = new SalleRepositoryImpl(entityManager);
    }

    @Override
    public List<Salle> getAvailableRooms(LocalDateTime dateDebut, LocalDateTime dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Il faut une date de debut et une date de fin");
        }
        controlerCreneau(dateDebut, dateFin);
        return salleRepository.findAvailableRooms(dateDebut, dateFin);
    }

    @Override
    public List<Salle> searchRooms(CritereRecherche critere, int numeroPage, int taillePage) {
        controlerCritere(critere);
        controlerPage(numeroPage, taillePage);
        return salleRepository.searchRooms(critere, numeroPage, taillePage);
    }

    @Override
    public long countRooms(CritereRecherche critere) {
        controlerCritere(critere);
        return salleRepository.countRooms(critere);
    }

    @Override
    public PaginationResult<Salle> searchRoomsPaginated(CritereRecherche critere, int numeroPage, int taillePage) {
        List<Salle> salles = searchRooms(critere, numeroPage, taillePage);
        return new PaginationResult<>(salles, numeroPage, taillePage, countRooms(critere));
    }

    private static void controlerCritere(CritereRecherche critere) {
        if (critere == null) throw new IllegalArgumentException("Le critere de recherche est obligatoire");
        if (critere.getDateDebut() != null && critere.getDateFin() != null) {
            controlerCreneau(critere.getDateDebut(), critere.getDateFin());
        }
        if (critere.getPlacesMin() != null && critere.getPlacesMin() < 0) {
            throw new IllegalArgumentException("Le nombre de places minimum ne peut pas etre negatif");
        }
    }

    // un creneau vide ou inverse n'a pas de sens
    private static void controlerCreneau(LocalDateTime dateDebut, LocalDateTime dateFin) {
        if (!dateDebut.isBefore(dateFin)) {
            throw new IllegalArgumentException("Creneau invalide : " + dateDebut + " doit preceder " + dateFin);
        }
    }

    private static void controlerPage(int numeroPage, int taillePage) {
        if (numeroPage < 1) throw new IllegalArgumentException("Les pages sont numerotees a partir de 1 (recu : " + numeroPage + ")");
        if (taillePage < 1) throw new IllegalArgumentException("Une page contient au moins un element (recu : " + taillePage + ")");
    }
}
