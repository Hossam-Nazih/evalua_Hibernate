package org.example.service;

import org.example.entities.Categorie;
import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;
import org.example.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Exercice de synthese : chiffre d'affaires mensuel par categorie, calcule avec l'API Criteria.
 */
public class RapportVentesService {
    private final EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

    public List<LigneRapportVentes> rapportMoisPrecedent() {
        return rapportMensuel(YearMonth.now().minusMonths(1));
    }

    /**
     * Pour chaque categorie : SUM(prix * quantite) des lignes de commande du mois donne.
     * Les categories sans vente apparaissent avec un chiffre d'affaires de 0.
     */
    public List<LigneRapportVentes> rapportMensuel(YearMonth mois) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();

            // commande_produit -> commandes, commande_produit -> produits -> categories
            Root<CommandeProduit> ligne = cq.from(CommandeProduit.class);
            Join<CommandeProduit, Commande> commande = ligne.join("commande");
            Join<CommandeProduit, Produit> produit = ligne.join("produit");
            Join<Produit, Categorie> categorie = produit.join("categorie");

            Expression<Number> montantLigne = cb.prod(produit.<BigDecimal>get("prix"), ligne.<Integer>get("quantite"));
            Expression<Number> chiffreAffaires = cb.sum(montantLigne);

            LocalDate debut = mois.atDay(1);
            LocalDate fin = mois.atEndOfMonth();
            cq.multiselect(categorie.get("nom").alias("categorie"), chiffreAffaires.alias("ca"))
              .where(cb.between(commande.<LocalDate>get("dateCommande"), debut, fin))
              .groupBy(categorie.get("id"), categorie.get("nom"));

            Map<String, BigDecimal> caParCategorie = new LinkedHashMap<>();
            for (Categorie c : em.createQuery("SELECT c FROM Categorie c ORDER BY c.nom", Categorie.class).getResultList()) {
                caParCategorie.put(c.getNom(), BigDecimal.ZERO);
            }
            for (Tuple t : em.createQuery(cq).getResultList()) {
                caParCategorie.put(t.get("categorie", String.class), enBigDecimal(t.get("ca", Number.class)));
            }

            List<LigneRapportVentes> rapport = new ArrayList<>();
            caParCategorie.forEach((nom, ca) -> rapport.add(new LigneRapportVentes(nom, ca)));
            rapport.sort(Comparator.comparing(LigneRapportVentes::getChiffreAffaires).reversed());
            return rapport;
        } finally {
            em.close();
        }
    }

    private static BigDecimal enBigDecimal(Number n) {
        if (n == null) return BigDecimal.ZERO;
        return n instanceof BigDecimal ? (BigDecimal) n : new BigDecimal(n.toString());
    }
}
