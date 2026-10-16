package org.example.service;

import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;
import org.example.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public class CommandeService extends CrudService<Commande, Long> {
    private final EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

    public CommandeService() {
        super(Commande.class);
    }

    /**
     * Les produits d'une commande viennent en general d'un autre EntityManager (getById) et sont detaches.
     * Avec @MapsId, Hibernate refuse un produit detache au persist ("detached entity passed to persist") :
     * on le remplace donc par une reference geree par l'EntityManager courant.
     */
    @Override
    public void create(Commande commande) {
        if (commande == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
        if (commande.getLignes().isEmpty()) {
            throw new IllegalArgumentException("Une commande doit contenir au moins un produit");
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (CommandeProduit ligne : commande.getLignes()) {
                if (ligne.getQuantite() <= 0) {
                    throw new IllegalArgumentException("Quantité invalide pour " + ligne.getProduit().getLibelle());
                }
                ligne.setProduit(em.getReference(Produit.class, ligne.getProduit().getId()));
            }
            em.persist(commande);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
