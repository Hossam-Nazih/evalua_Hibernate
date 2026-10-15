package com.example.repository;

import com.example.model.Equipement;
import com.example.model.Reservation;
import com.example.model.Salle;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository base sur JPA. Hors serveur d'applications, l'EntityManager est recu au constructeur
 * (dans un serveur Java EE : @Stateless + @PersistenceContext(unitName = "SallePU")).
 */
public class SalleRepositoryImpl implements SalleRepository {

    private final EntityManager entityManager;

    public SalleRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Salle> findAvailableRooms(LocalDateTime dateDebut, LocalDateTime dateFin) {
        // sous-requete = salles occupees ; deux creneaux se croisent si chacun commence avant la fin de l'autre
        String jpql = "SELECT s FROM Salle s WHERE s.idSalle NOT IN ("
                + "SELECT r.salle.idSalle FROM Reservation r"
                + " WHERE :dateDebut < r.dateFin AND :dateFin > r.dateDebut"
                + ") ORDER BY s.libelle";
        return entityManager.createQuery(jpql, Salle.class)
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", dateFin)
                .getResultList();
    }

    @Override
    public List<Salle> searchRooms(CritereRecherche critere, int numeroPage, int taillePage) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Salle> requete = cb.createQuery(Salle.class);
        Root<Salle> salle = requete.from(Salle.class);

        // on trie toujours : sans ordre fixe, une salle pourrait apparaitre sur deux pages
        requete.select(salle)
                .where(filtres(cb, requete, salle, critere))
                .orderBy(cb.asc(salle.get("libelle")));

        return entityManager.createQuery(requete)
                .setFirstResult((numeroPage - 1) * taillePage)   // OFFSET : lignes des pages precedentes
                .setMaxResults(taillePage)                       // LIMIT : lignes de la page demandee
                .getResultList();
    }

    @Override
    public long countRooms(CritereRecherche critere) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> comptage = cb.createQuery(Long.class);
        Root<Salle> salle = comptage.from(Salle.class);

        comptage.select(cb.count(salle))
                .where(filtres(cb, comptage, salle, critere));

        return entityManager.createQuery(comptage).getSingleResult();
    }

    /**
     * Seule source des filtres : la recherche et le comptage l'appellent toutes les deux,
     * ce qui garantit que le total correspond bien aux pages affichees.
     */
    private Predicate filtres(CriteriaBuilder cb, AbstractQuery<?> requete, Root<Salle> salle, CritereRecherche critere) {
        List<Predicate> conditions = new ArrayList<>();

        if (critere.getLibelle() != null && !critere.getLibelle().isEmpty()) {
            conditions.add(cb.like(cb.lower(salle.get("libelle")), "%" + critere.getLibelle().toLowerCase() + "%"));
        }

        if (critere.getPlacesMin() != null) {
            conditions.add(cb.ge(salle.get("nbPlaces"), critere.getPlacesMin()));
        }

        // jointure vers Batiment uniquement si le critere est utilise
        if (critere.getBatiment() != null) {
            conditions.add(cb.equal(salle.join("batiment").get("nomBatiment"), critere.getBatiment()));
        }

        // sous-requete plutot que jointure : une salle n'est jamais comptee deux fois
        if (critere.getEquipement() != null) {
            Subquery<Long> equipees = requete.subquery(Long.class);
            Root<Salle> autreSalle = equipees.from(Salle.class);
            Join<Salle, Equipement> equipement = autreSalle.join("equipements");
            equipees.select(autreSalle.get("idSalle"))
                    .where(cb.equal(equipement.get("designation"), critere.getEquipement()));
            conditions.add(salle.get("idSalle").in(equipees));
        }

        // meme regle de chevauchement que la requete JPQL, ecrite avec l'API Criteria
        if (critere.getDateDebut() != null && critere.getDateFin() != null) {
            Subquery<Long> occupees = requete.subquery(Long.class);
            Root<Reservation> reservation = occupees.from(Reservation.class);
            occupees.select(reservation.get("salle").get("idSalle"))
                    .where(cb.lessThan(reservation.get("dateDebut"), critere.getDateFin()),
                           cb.greaterThan(reservation.get("dateFin"), critere.getDateDebut()));
            conditions.add(cb.not(salle.get("idSalle").in(occupees)));
        }

        // sans condition, cb.and() vaut vrai : toutes les salles sont retournees
        return cb.and(conditions.toArray(new Predicate[0]));
    }
}
