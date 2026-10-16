package ma.projet.test;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.service.MariageService;
import ma.projet.util.HibernateUtil;

import java.time.LocalDate;

/**
 * Programme de test de l'exercice 3, dans l'ordre demande par l'enonce.
 */
public class TestEtatCivil {

    public static void main(String[] args) {
        HommeService hommeService = new HommeService();
        FemmeService femmeService = new FemmeService();

        try {
            creerJeuDeDonnees();
            Homme safi = hommeService.findAll().get(0);
            Femme karima = femmeService.findAll().get(3);
            LocalDate debut = LocalDate.of(1985, 1, 1);
            LocalDate fin = LocalDate.of(2005, 12, 31);

            titre("Liste des femmes");
            femmeService.findAll().forEach(System.out::println);

            titre("Femme la plus âgée");
            System.out.println(femmeService.findPlusAgee());

            titre("Épouses de " + safi.getNom() + " " + safi.getPrenom() + " entre 1985 et 2005");
            hommeService.findEpousesEntre(safi, debut, fin).forEach(System.out::println);

            titre("Nombre d'enfants de " + karima.getPrenom() + " " + karima.getNom() + " entre 1985 et 2005 (requête native nommée)");
            System.out.println(femmeService.nombreEnfantsEntre(karima, debut, fin));

            titre("Femmes mariées au moins deux fois (requête nommée)");
            femmeService.findMarieesAuMoinsDeuxFois().forEach(System.out::println);

            titre("Hommes mariés à quatre femmes entre 1985 et 2005 (API Criteria)");
            System.out.println("Nombre : " + hommeService.nombreHommesMariesAQuatreFemmesEntre(debut, fin));
            hommeService.findHommesMariesAQuatreFemmesEntre(debut, fin).forEach(System.out::println);

            titre("Mariages d'un homme avec tous les détails");
            hommeService.afficherMariages(safi);
        } finally {
            HibernateUtil.shutdown();
        }
    }

    /**
     * 10 femmes, 5 hommes et leurs mariages.
     * - SAFI Said : 4 epouses entre 1989 et 2000 (dont un mariage termine), c'est l'exemple de l'enonce
     * - TAZI Youssef : 4 epouses aussi, mais la premiere en 1980, hors de la periode 1985-2005
     * - Karima ALAMI et Nora HAJJI se sont mariees deux fois
     */
    public static void creerJeuDeDonnees() {
        HommeService hommeService = new HommeService();
        FemmeService femmeService = new FemmeService();
        MariageService mariageService = new MariageService();

        Homme safi = new Homme("SAFI", "Said", "0661111111", "Rabat", LocalDate.of(1965, 4, 12));
        Homme bennani = new Homme("BENNANI", "Omar", "0662222222", "Fès", LocalDate.of(1962, 8, 3));
        Homme tazi = new Homme("TAZI", "Youssef", "0663333333", "Casablanca", LocalDate.of(1955, 1, 20));
        Homme fassi = new Homme("FASSI", "Hamza", "0664444444", "Meknès", LocalDate.of(1980, 11, 9));
        Homme kabbaj = new Homme("KABBAJ", "Mehdi", "0665555555", "Tanger", LocalDate.of(1950, 6, 30));
        for (Homme h : new Homme[]{safi, bennani, tazi, fassi, kabbaj}) {
            hommeService.create(h);
        }

        Femme salima = new Femme("RAMI", "Salima", "0671000001", "Rabat", LocalDate.of(1968, 2, 14));
        Femme amal = new Femme("ALI", "Amal", "0671000002", "Salé", LocalDate.of(1972, 7, 1));
        Femme wafa = new Femme("ALAOUI", "Wafa", "0671000003", "Rabat", LocalDate.of(1976, 3, 22));
        Femme karima = new Femme("ALAMI", "Karima", "0671000004", "Kénitra", LocalDate.of(1967, 10, 5));
        Femme nora = new Femme("HAJJI", "Nora", "0671000005", "Fès", LocalDate.of(1970, 12, 18));
        Femme leila = new Femme("SBAI", "Leila", "0671000006", "Casablanca", LocalDate.of(1958, 5, 9));
        Femme houda = new Femme("NACIRI", "Houda", "0671000007", "Casablanca", LocalDate.of(1974, 9, 27));
        Femme sanaa = new Femme("CHERKAOUI", "Sanaa", "0671000008", "Mohammedia", LocalDate.of(1979, 1, 11));
        Femme imane = new Femme("ZIANI", "Imane", "0671000009", "Meknès", LocalDate.of(1985, 4, 4));
        Femme fatima = new Femme("BERRADA", "Fatima", "0671000010", "Tanger", LocalDate.of(1950, 2, 2));
        for (Femme f : new Femme[]{salima, amal, wafa, karima, nora, leila, houda, sanaa, imane, fatima}) {
            femmeService.create(f);
        }

        Mariage[] mariages = {
                new Mariage(safi, karima, LocalDate.of(1989, 9, 3), LocalDate.of(1990, 9, 3), 0),
                new Mariage(safi, salima, LocalDate.of(1990, 9, 3), null, 4),
                new Mariage(safi, amal, LocalDate.of(1995, 9, 3), null, 2),
                new Mariage(safi, wafa, LocalDate.of(2000, 11, 4), null, 3),
                new Mariage(bennani, karima, LocalDate.of(1992, 5, 10), null, 2),
                new Mariage(bennani, nora, LocalDate.of(1993, 1, 1), LocalDate.of(2001, 1, 1), 1),
                new Mariage(tazi, leila, LocalDate.of(1980, 1, 1), null, 3),
                new Mariage(tazi, houda, LocalDate.of(1999, 3, 3), null, 2),
                new Mariage(tazi, nora, LocalDate.of(2003, 6, 15), null, 1),
                new Mariage(tazi, sanaa, LocalDate.of(2004, 4, 4), null, 1),
                new Mariage(fassi, imane, LocalDate.of(2010, 7, 7), null, 0),
                new Mariage(kabbaj, fatima, LocalDate.of(1975, 2, 2), LocalDate.of(1985, 2, 2), 5)
        };
        for (Mariage m : mariages) {
            mariageService.create(m);
        }
    }

    private static void titre(String texte) {
        System.out.println();
        System.out.println("===== " + texte + " =====");
    }
}
