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
 * Exercice de synthese : chiffre d'affaires mensuel de chaque categorie, agrege par la base via l'API Criteria.
 */
public class RapportVentesService {
    private final EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

    public List<LigneRapportVentes> rapportMoisPrecedent() {
        return rapportMensuel(YearMonth.now().minusMonths(1));
    }

    /**
     * Equivalent SQL :
     *   SELECT cat.intitule, SUM(p.prix_unitaire * l.quantite_commandee)
     *   FROM commande_produit l JOIN commandes c ... JOIN produits p ... JOIN categories cat ...
     *   WHERE c.date_passage BETWEEN debut AND fin GROUP BY cat.id, cat.intitule
     * Le resultat est trie par chiffre d'affaires decroissant ; une categorie sans vente vaut 0.
     */
    public List<LigneRapportVentes> rapportMensuel(YearMonth mois) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            // Tuple : chaque ligne contient plusieurs valeurs lues par alias
            CriteriaQuery<Tuple> requete = cb.createTupleQuery();

            Root<CommandeProduit> ligne = requete.from(CommandeProduit.class);
            Join<CommandeProduit, Commande> commande = ligne.join("commande");
            Join<CommandeProduit, Produit> produit = ligne.join("produit");
            Join<Produit, Categorie> categorie = produit.join("categorie");

            // prod() multiplie deux expressions, sum() agrege par groupe
            Expression<Number> montantLigne = cb.prod(produit.<BigDecimal>get("prixUnitaire"),
                                                      ligne.<Integer>get("quantiteCommandee"));
            Expression<Number> totalCategorie = cb.sum(montantLigne);

            LocalDate premierJour = mois.atDay(1);
            LocalDate dernierJour = mois.atEndOfMonth();
            requete.multiselect(categorie.get("intitule").alias("categorie"), totalCategorie.alias("total"))
                   .where(cb.between(commande.<LocalDate>get("datePassage"), premierJour, dernierJour))
                   .groupBy(categorie.get("id"), categorie.get("intitule"));

            // toutes les categories partent de 0, puis les totaux trouves les remplacent
            Map<String, BigDecimal> totaux = new LinkedHashMap<>();
            for (Categorie c : em.createQuery("SELECT c FROM Categorie c ORDER BY c.intitule", Categorie.class).getResultList()) {
                totaux.put(c.getIntitule(), BigDecimal.ZERO);
            }
            for (Tuple t : em.createQuery(requete).getResultList()) {
                totaux.put(t.get("categorie", String.class), versBigDecimal(t.get("total", Number.class)));
            }

            List<LigneRapportVentes> rapport = new ArrayList<>();
            totaux.forEach((intitule, total) -> rapport.add(new LigneRapportVentes(intitule, total)));
            rapport.sort(Comparator.comparing(LigneRapportVentes::getChiffreAffaires).reversed());
            return rapport;
        } finally {
            em.close();
        }
    }

    // selon la base, la somme peut revenir en BigDecimal, Double ou Long
    private static BigDecimal versBigDecimal(Number valeur) {
        if (valeur == null) return BigDecimal.ZERO;
        return valeur instanceof BigDecimal ? (BigDecimal) valeur : new BigDecimal(valeur.toString());
    }
}
