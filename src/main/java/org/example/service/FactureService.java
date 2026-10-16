package org.example.service;

import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Facture;
import org.example.entities.ModePaiement;
import org.example.entities.Paiement;
import org.example.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Facturation des commandes et suivi de leurs reglements.
 */
public class FactureService extends CrudService<Facture, Long> {
    private static final DateTimeFormatter ANNEE_MOIS = DateTimeFormatter.ofPattern("yyyyMM");

    private final EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

    public FactureService() {
        super(Facture.class);
    }

    /**
     * Cree la facture d'une commande. Le montant est recalcule a partir des lignes
     * (prix unitaire x quantite) ; une commande deja facturee est refusee.
     */
    public Facture facturer(Long idCommande) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Commande commande = em.find(Commande.class, idCommande);
            if (commande == null) {
                throw new IllegalArgumentException("Commande introuvable : " + idCommande);
            }
            boolean dejaFacturee = !em.createNamedQuery("Facture.parCommande", Facture.class)
                    .setParameter("idCommande", idCommande)
                    .getResultList().isEmpty();
            if (dejaFacturee) {
                throw new IllegalStateException("La commande " + idCommande + " est déjà facturée");
            }

            // JOIN FETCH : les produits des lignes arrivent dans la meme requete (pas de N+1)
            List<CommandeProduit> lignes = em.createQuery(
                    "SELECT l FROM CommandeProduit l JOIN FETCH l.produit WHERE l.commande.id = :idCommande",
                    CommandeProduit.class)
                    .setParameter("idCommande", idCommande)
                    .getResultList();
            BigDecimal montant = BigDecimal.ZERO;
            for (CommandeProduit ligne : lignes) {
                montant = montant.add(ligne.getProduit().getPrixUnitaire()
                        .multiply(BigDecimal.valueOf(ligne.getQuantiteCommandee())));
            }

            LocalDate aujourdhui = LocalDate.now();
            String numero = "FAC-" + aujourdhui.format(ANNEE_MOIS) + "-" + idCommande;
            Facture facture = new Facture(numero, aujourdhui, montant, commande);
            em.persist(facture);
            tx.commit();
            return facture;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Enregistre un reglement ; il doit etre positif et ne pas depasser ce qui reste du.
     */
    public Paiement enregistrerPaiement(Long idFacture, BigDecimal montant, ModePaiement mode) {
        if (montant == null || montant.signum() <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit être positif");
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Facture facture = em.find(Facture.class, idFacture);
            if (facture == null) {
                throw new IllegalArgumentException("Facture introuvable : " + idFacture);
            }
            BigDecimal reste = facture.getMontantTTC().subtract(montantRegle(em, idFacture));
            if (montant.compareTo(reste) > 0) {
                throw new IllegalArgumentException("Paiement de " + montant + " supérieur au reste dû (" + reste + ")");
            }
            Paiement paiement = new Paiement(montant, LocalDate.now(), mode);
            facture.ajouterReglement(paiement);
            em.persist(paiement);
            tx.commit();
            return paiement;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public BigDecimal resteAPayer(Long idFacture) {
        EntityManager em = emf.createEntityManager();
        try {
            Facture facture = em.find(Facture.class, idFacture);
            if (facture == null) {
                throw new IllegalArgumentException("Facture introuvable : " + idFacture);
            }
            return facture.getMontantTTC().subtract(montantRegle(em, idFacture));
        } finally {
            em.close();
        }
    }

    private static BigDecimal montantRegle(EntityManager em, Long idFacture) {
        Number total = em.createNamedQuery("Facture.montantRegle", Number.class)
                .setParameter("idFacture", idFacture)
                .getSingleResult();
        return total instanceof BigDecimal ? (BigDecimal) total : new BigDecimal(total.toString());
    }
}
