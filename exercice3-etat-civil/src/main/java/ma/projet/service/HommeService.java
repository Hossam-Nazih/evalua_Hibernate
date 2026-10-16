package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.dao.AbstractDao;
import ma.projet.util.Formats;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HommeService extends AbstractDao<Homme> {

    public HommeService() {
        super(Homme.class);
    }

    // ---------- epouses d'un homme entre deux dates ----------

    // femmes dont le mariage avec cet homme a commence dans la periode
    public List<Femme> findEpousesEntre(Homme homme, LocalDate debut, LocalDate fin) {
        return lire(session -> session.createQuery(
                        "SELECT f FROM Mariage m JOIN m.femme f "
                                + "WHERE m.homme.id = :id AND m.dateDebut BETWEEN :debut AND :fin ORDER BY m.dateDebut", Femme.class)
                .setParameter("id", homme.getId())
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .list());
    }

    // ---------- API Criteria : hommes maries a quatre femmes entre deux dates ----------

    /**
     * Equivalent JPQL :
     *   SELECT COUNT(h) FROM Homme h
     *   WHERE (SELECT COUNT(DISTINCT m.femme) FROM Mariage m
     *          WHERE m.homme = h AND m.dateDebut BETWEEN :debut AND :fin) = 4
     */
    public long nombreHommesMariesAQuatreFemmesEntre(LocalDate debut, LocalDate fin) {
        return lire(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> requete = cb.createQuery(Long.class);
            Root<Homme> homme = requete.from(Homme.class);
            requete.select(cb.count(homme)).where(cb.equal(nombreEpousesEntre(cb, requete, homme, debut, fin), 4L));
            return session.createQuery(requete).getSingleResult();
        });
    }

    // meme critere, pour afficher qui sont ces hommes
    public List<Homme> findHommesMariesAQuatreFemmesEntre(LocalDate debut, LocalDate fin) {
        return lire(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Homme> requete = cb.createQuery(Homme.class);
            Root<Homme> homme = requete.from(Homme.class);
            requete.select(homme)
                   .where(cb.equal(nombreEpousesEntre(cb, requete, homme, debut, fin), 4L))
                   .orderBy(cb.asc(homme.get("nom")));
            return session.createQuery(requete).getResultList();
        });
    }

    // sous-requete : nombre de femmes differentes epousees par l'homme courant dans la periode
    private static Subquery<Long> nombreEpousesEntre(CriteriaBuilder cb, CriteriaQuery<?> requete, Root<Homme> homme,
                                                     LocalDate debut, LocalDate fin) {
        Subquery<Long> sousRequete = requete.subquery(Long.class);
        Root<Mariage> mariage = sousRequete.from(Mariage.class);
        sousRequete.select(cb.countDistinct(mariage.get("femme")))
                   .where(cb.equal(mariage.get("homme"), homme),
                          cb.between(mariage.<LocalDate>get("dateDebut"), debut, fin));
        return sousRequete;
    }

    // ---------- mariages d'un homme avec les details ----------

    public List<Mariage> findMariages(Homme homme) {
        return lire(session -> session.createQuery(
                        "SELECT m FROM Mariage m JOIN FETCH m.femme WHERE m.homme.id = :id ORDER BY m.dateDebut", Mariage.class)
                .setParameter("id", homme.getId())
                .list());
    }

    /**
     * Texte au format demande :
     * Nom : SAFI SAID
     * Mariages En Cours :
     * 1. Femme : SALIMA RAMI   Date Début : 03/09/1990    Nbr Enfants : 4
     *
     * Mariages échoués :
     * 1. Femme : KARIMA ALAMI  Date Début : 03/09/1989    Date Fin : 03/09/1990    Nbr Enfants : 0
     */
    public String detailMariages(Homme homme) {
        List<Mariage> mariages = findMariages(homme);
        List<Mariage> enCours = mariages.stream().filter(Mariage::estEnCours).collect(Collectors.toList());
        List<Mariage> echoues = mariages.stream().filter(m -> !m.estEnCours()).collect(Collectors.toList());

        StringBuilder texte = new StringBuilder();
        texte.append(String.format("Nom : %s %s%n", homme.getNom().toUpperCase(), homme.getPrenom().toUpperCase()));
        texte.append(String.format("Mariages En Cours :%n"));
        int numero = 1;
        for (Mariage m : enCours) {
            texte.append(String.format("%d. Femme : %-14sDate Début : %-14sNbr Enfants : %d%n",
                    numero++, nomFemme(m.getFemme()), Formats.dateCourte(m.getDateDebut()), m.getNbrEnfant()));
        }
        texte.append(String.format("%nMariages échoués :%n"));
        numero = 1;
        for (Mariage m : echoues) {
            texte.append(String.format("%d. Femme : %-14sDate Début : %-14sDate Fin : %-14sNbr Enfants : %d%n",
                    numero++, nomFemme(m.getFemme()), Formats.dateCourte(m.getDateDebut()),
                    Formats.dateCourte(m.getDateFin()), m.getNbrEnfant()));
        }
        return texte.toString();
    }

    public void afficherMariages(Homme homme) {
        System.out.print(detailMariages(homme));
    }

    // SALIMA RAMI : prenom puis nom, comme dans l'exemple de l'enonce
    private static String nomFemme(Femme femme) {
        return (femme.getPrenom() + " " + femme.getNom()).toUpperCase();
    }
}
