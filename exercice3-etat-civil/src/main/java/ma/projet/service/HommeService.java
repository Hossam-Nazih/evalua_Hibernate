package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.dao.AbstractDao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HommeService extends AbstractDao<Homme> {
    private static final DateTimeFormatter JJ_MM_AAAA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public HommeService() {
        super(Homme.class);
    }

    // ===== Epouses sur une periode =====

    // epouses dont l'union avec cet homme a ete celebree dans l'intervalle, dans l'ordre chronologique
    public List<Femme> findEpousesEntre(Homme homme, LocalDate du, LocalDate au) {
        return consulter(session -> session.createQuery(
                        "SELECT f FROM Mariage m JOIN m.epouse f "
                                + "WHERE m.epoux.id = :idHomme AND m.dateUnion BETWEEN :du AND :au ORDER BY m.dateUnion", Femme.class)
                .setParameter("idHomme", homme.getId())
                .setParameter("du", du)
                .setParameter("au", au)
                .list());
    }

    // ===== API Criteria : hommes ayant epouse quatre femmes sur la periode =====

    /**
     * Traduction JPQL de la requete construite :
     *   SELECT COUNT(h) FROM Homme h
     *   WHERE (SELECT COUNT(DISTINCT m.epouse) FROM Mariage m
     *          WHERE m.epoux = h AND m.dateUnion BETWEEN :du AND :au) = 4
     */
    public long nombreHommesMariesAQuatreFemmesEntre(LocalDate du, LocalDate au) {
        return consulter(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> requete = cb.createQuery(Long.class);
            Root<Homme> h = requete.from(Homme.class);
            requete.select(cb.count(h)).where(cb.equal(epousesDistinctes(cb, requete, h, du, au), 4L));
            return session.createQuery(requete).getSingleResult();
        });
    }

    // meme filtre, mais on recupere les hommes pour les afficher
    public List<Homme> findHommesMariesAQuatreFemmesEntre(LocalDate du, LocalDate au) {
        return consulter(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Homme> requete = cb.createQuery(Homme.class);
            Root<Homme> h = requete.from(Homme.class);
            requete.select(h)
                   .where(cb.equal(epousesDistinctes(cb, requete, h, du, au), 4L))
                   .orderBy(cb.asc(h.get("nomFamille")));
            return session.createQuery(requete).getResultList();
        });
    }

    // sous-requete correlee : nombre d'epouses differentes de l'homme h dont l'union tombe dans l'intervalle
    private static Subquery<Long> epousesDistinctes(CriteriaBuilder cb, CriteriaQuery<?> requete, Root<Homme> h,
                                                    LocalDate du, LocalDate au) {
        Subquery<Long> sousRequete = requete.subquery(Long.class);
        Root<Mariage> m = sousRequete.from(Mariage.class);
        sousRequete.select(cb.countDistinct(m.get("epouse")))
                   .where(cb.equal(m.get("epoux"), h),
                          cb.between(m.<LocalDate>get("dateUnion"), du, au));
        return sousRequete;
    }

    // ===== Unions d'un homme, avec details =====

    public List<Mariage> findMariages(Homme homme) {
        return consulter(session -> session.createQuery(
                        "SELECT m FROM Mariage m JOIN FETCH m.epouse WHERE m.epoux.id = :idHomme ORDER BY m.dateUnion", Mariage.class)
                .setParameter("idHomme", homme.getId())
                .list());
    }

    /**
     * Bloc de texte au format de l'enonce :
     * Nom : SAFI SAID
     * Mariages En Cours :
     * 1. Femme : SALIMA RAMI   Date Début : 03/09/1990    Nbr Enfants : 4
     *
     * Mariages échoués :
     * 1. Femme : KARIMA ALAMI  Date Début : 03/09/1989    Date Fin : 03/09/1990    Nbr Enfants : 0
     */
    public String detailMariages(Homme homme) {
        List<Mariage> unions = findMariages(homme);
        List<Mariage> enCours = unions.stream().filter(Mariage::estEnCours).collect(Collectors.toList());
        List<Mariage> dissoutes = unions.stream().filter(m -> !m.estEnCours()).collect(Collectors.toList());

        StringBuilder bloc = new StringBuilder();
        bloc.append(String.format("Nom : %s %s%n", homme.getNomFamille().toUpperCase(), homme.getPrenomUsuel().toUpperCase()));
        bloc.append(String.format("Mariages En Cours :%n"));
        int rang = 1;
        for (Mariage m : enCours) {
            bloc.append(String.format("%d. Femme : %-14sDate Début : %-14sNbr Enfants : %d%n",
                    rang++, identite(m.getEpouse()), m.getDateUnion().format(JJ_MM_AAAA), m.getNombreEnfants()));
        }
        bloc.append(String.format("%nMariages échoués :%n"));
        rang = 1;
        for (Mariage m : dissoutes) {
            bloc.append(String.format("%d. Femme : %-14sDate Début : %-14sDate Fin : %-14sNbr Enfants : %d%n",
                    rang++, identite(m.getEpouse()), m.getDateUnion().format(JJ_MM_AAAA),
                    m.getDateDivorce().format(JJ_MM_AAAA), m.getNombreEnfants()));
        }
        return bloc.toString();
    }

    public void afficherMariages(Homme homme) {
        System.out.print(detailMariages(homme));
    }

    // l'enonce ecrit l'epouse "PRENOM NOM" (ex. SALIMA RAMI)
    private static String identite(Femme femme) {
        return (femme.getPrenomUsuel() + " " + femme.getNomFamille()).toUpperCase();
    }
}
