package org.example.service;

import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;
import org.example.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * Service des commandes : impose au moins une ligne et des quantites positives.
 */
public class CommandeService extends CrudService<Commande, Long> {
    private final EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

    public CommandeService() {
        super(Commande.class);
    }

    /**
     * Les produits ajoutes a la commande ont ete lus par un autre EntityManager, ils sont donc detaches.
     * Avec une cle derivee (@MapsId), Hibernate rejette un produit detache lors du persist :
     * chaque ligne recoit a la place une reference geree, obtenue par getReference() sans requete SQL.
     */
    @Override
    public void create(Commande commande) {
        exigerEntite(commande);
        if (commande.getLignesCommande().isEmpty()) {
            throw new IllegalArgumentException("Une commande doit contenir au moins un produit");
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (CommandeProduit ligne : commande.getLignesCommande()) {
                if (ligne.getQuantiteCommandee() <= 0) {
                    throw new IllegalArgumentException("Quantité invalide pour " + ligne.getProduit().getDesignation());
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
