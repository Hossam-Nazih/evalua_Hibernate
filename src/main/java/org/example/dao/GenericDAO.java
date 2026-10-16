package org.example.dao;

import org.example.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;

/**
 * Operations CRUD communes a toutes les entites, ecrites avec l'API JPA standard.
 * Chaque appel represente une unite de travail : il ouvre son propre EntityManager et le referme
 * quoi qu'il arrive ; les ecritures sont encadrees par une transaction annulee en cas d'erreur.
 *
 * @param <T>  type de l'entite geree
 * @param <ID> type de sa cle primaire
 */
public class GenericDAO<T, ID extends Serializable> {
    private final Class<T> typeEntite;
    private final EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

    public GenericDAO(Class<T> typeEntite) {
        this.typeEntite = typeEntite;
    }

    public T find(ID id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(typeEntite, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            // le nom simple de la classe est aussi le nom de l'entite en JPQL
            String jpql = "SELECT e FROM " + typeEntite.getSimpleName() + " e";
            return em.createQuery(jpql, typeEntite).getResultList();
        } finally {
            em.close();
        }
    }

    public void create(T entite) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entite);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public T update(T entite) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // merge renvoie la copie geree ; l'objet passe en parametre reste detache
            T copieGeree = em.merge(entite);
            tx.commit();
            return copieGeree;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(T entite) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // remove() n'accepte qu'une entite geree : on rattache d'abord celle recue si besoin
            T geree = em.contains(entite) ? entite : em.merge(entite);
            em.remove(geree);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
