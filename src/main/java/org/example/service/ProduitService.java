package org.example.service;

import org.example.entities.Produit;
import org.example.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service du catalogue : CRUD herite + requetes nommees, recherche dynamique et pagination.
 */
public class ProduitService extends CrudService<Produit, Long> {
    private final EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

    public ProduitService() {
        super(Produit.class);
    }

    // renvoie le produit avec sa categorie deja initialisee, ou null s'il n'existe pas
    public Produit getByIdAvecCategorie(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Produit.avecCategorie", Produit.class)
                     .setParameter("id", id)
                     .getResultStream()
                     .findFirst()
                     .orElse(null);
        } finally {
            em.close();
        }
    }

    public List<Produit> findByCategorie(Long idCategorie) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Produit.parCategorie", Produit.class)
                     .setParameter("idCategorie", idCategorie)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    // recherche insensible a la casse sur une partie de la designation
    public List<Produit> searchByDesignation(String motCle) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Produit.parDesignation", Produit.class)
                     .setParameter("motCle", motCle)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Recherche multi-filtres avec l'API Criteria : chaque parametre null ou vide est ignore,
     * les autres deviennent des conditions reliees par AND. Les valeurs passent en parametres JDBC.
     */
    public List<Produit> search(String motCle, BigDecimal prixMin, BigDecimal prixMax) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Produit> requete = cb.createQuery(Produit.class);
            Root<Produit> produit = requete.from(Produit.class);
            List<Predicate> conditions = new ArrayList<>();
            if (motCle != null && !motCle.isEmpty()) {
                conditions.add(cb.like(cb.lower(produit.<String>get("designation")), "%" + motCle.toLowerCase() + "%"));
            }
            if (prixMin != null) {
                conditions.add(cb.ge(produit.<BigDecimal>get("prixUnitaire"), prixMin));
            }
            if (prixMax != null) {
                conditions.add(cb.le(produit.<BigDecimal>get("prixUnitaire"), prixMax));
            }
            requete.where(conditions.toArray(new Predicate[0]));
            requete.orderBy(cb.asc(produit.get("prixUnitaire")));
            return em.createQuery(requete).getResultList();
        } finally {
            em.close();
        }
    }

    // numeroPage commence a 1 ; le tri sur l'id garantit un decoupage stable d'une page a l'autre
    public List<Produit> findPage(int numeroPage, int taillePage) {
        if (numeroPage < 1 || taillePage < 1) {
            throw new IllegalArgumentException("numeroPage et taillePage doivent être >= 1");
        }
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produit p ORDER BY p.id", Produit.class)
                     .setFirstResult((numeroPage - 1) * taillePage)
                     .setMaxResults(taillePage)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Produit p", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Insertion groupee en tout ou rien : un seul prix invalide et la transaction entiere est annulee,
     * y compris les produits deja passes a persist().
     */
    public void createAll(List<Produit> produits) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (Produit p : produits) {
                em.persist(p);
                if (p.getPrixUnitaire() == null || p.getPrixUnitaire().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Prix négatif ou absent pour " + p.getDesignation());
                }
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
