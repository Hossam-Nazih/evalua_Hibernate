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
 * CRUD generique plus les requetes propres aux produits (named queries, Criteria, pagination).
 */
public class ProduitService extends CrudService<Produit, Long> {
    private final EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

    public ProduitService() {
        super(Produit.class);
    }

    // produit et categorie charges ensemble (JOIN FETCH) : utilisable une fois l'EntityManager ferme
    public Produit getByIdAvecCategorie(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Produit.findByIdAvecCategorie", Produit.class)
                     .setParameter("id", id)
                     .getResultStream()
                     .findFirst()
                     .orElse(null);
        } finally {
            em.close();
        }
    }

    public List<Produit> findByCategorie(Long catId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Produit.findByCategorie", Produit.class)
                     .setParameter("catId", catId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Produit> searchByLibelle(String term) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Produit.searchByLibelle", Produit.class)
                     .setParameter("term", term)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    // recherche multi-criteres : seuls les filtres renseignes sont ajoutes a la clause WHERE
    public List<Produit> search(String term, BigDecimal minPrix, BigDecimal maxPrix) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Produit> cq = cb.createQuery(Produit.class);
            Root<Produit> root = cq.from(Produit.class);
            List<Predicate> predicates = new ArrayList<>();
            if (term != null && !term.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get("libelle")), "%" + term.toLowerCase() + "%"));
            }
            if (minPrix != null) {
                predicates.add(cb.ge(root.<BigDecimal>get("prix"), minPrix));
            }
            if (maxPrix != null) {
                predicates.add(cb.le(root.<BigDecimal>get("prix"), maxPrix));
            }
            cq.where(predicates.toArray(new Predicate[0]));
            cq.orderBy(cb.asc(root.get("prix")));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    // pageNumber commence a 1
    public List<Produit> findPage(int pageNumber, int pageSize) {
        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException("pageNumber et pageSize doivent être >= 1");
        }
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produit p ORDER BY p.id", Produit.class)
                     .setFirstResult((pageNumber - 1) * pageSize)
                     .setMaxResults(pageSize)
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
     * Enregistre tous les produits dans une seule transaction : si l'un d'eux a un prix negatif,
     * aucun n'est enregistre (rollback).
     */
    public void createAll(List<Produit> produits) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (Produit p : produits) {
                em.persist(p);
                if (p.getPrix() == null || p.getPrix().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Prix négatif ou absent pour " + p.getLibelle());
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
