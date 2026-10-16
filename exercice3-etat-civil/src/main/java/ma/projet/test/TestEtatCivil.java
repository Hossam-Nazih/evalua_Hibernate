package ma.projet.test;

import ma.projet.beans.Enfant;
import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.beans.Province;
import ma.projet.service.EnfantService;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.service.MariageService;
import ma.projet.service.ProvinceService;
import ma.projet.util.ConsoleH2;
import ma.projet.util.HibernateUtil;

import java.time.LocalDate;

/**
 * Exercice 3 : deroule le programme de test de l'enonce, etape par etape.
 * La console H2 reste ouverte a la fin pour inspecter les tables.
 */
public class TestEtatCivil {

    public static void main(String[] args) {
        ConsoleH2.demarrer("etat_civil");
        try {
            JeuDeDonnees d = JeuDeDonnees.charger();
            HommeService hommeService = new HommeService();
            FemmeService femmeService = new FemmeService();
            LocalDate du = LocalDate.of(1985, 1, 1);
            LocalDate au = LocalDate.of(2005, 12, 31);

            entete("1. Liste des femmes");
            femmeService.findAll().forEach(System.out::println);

            entete("2. Femme la plus âgée");
            System.out.println(femmeService.findPlusAgee());

            entete("3. Épouses de SAFI Said entre 1985 et 2005");
            hommeService.findEpousesEntre(d.safi, du, au).forEach(System.out::println);

            entete("4. Enfants de Karima ALAMI entre 1985 et 2005 (requête native nommée)");
            System.out.println(femmeService.nombreEnfantsEntre(d.karima, du, au));

            entete("5. Femmes mariées deux fois ou plus (requête nommée)");
            femmeService.findMarieesAuMoinsDeuxFois().forEach(System.out::println);

            entete("6. Hommes mariés à quatre femmes entre 1985 et 2005 (API Criteria)");
            System.out.println("Nombre : " + hommeService.nombreHommesMariesAQuatreFemmesEntre(du, au));
            hommeService.findHommesMariesAQuatreFemmesEntre(du, au).forEach(System.out::println);

            entete("7. Mariages de SAFI Said avec les détails");
            hommeService.afficherMariages(d.safi);

            entete("Nouvelles entités : provinces et enfants");
            ProvinceService provinceService = new ProvinceService();
            for (Province p : provinceService.findAll()) {
                System.out.println(p.getNomProvince() + " : " + provinceService.nombreHabitants(p) + " habitant(s)");
            }
            System.out.println("Enfants de Salima RAMI :");
            new EnfantService().findEnfantsDeLaMere(d.salima).forEach(e -> System.out.println("  " + e));

            ConsoleH2.attendreEntreePuisArreter();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void entete(String texte) {
        System.out.println();
        System.out.println("----- " + texte + " -----");
    }

    /**
     * 10 femmes, 5 hommes, leurs unions et quelques enfants. Points de repere :
     * - SAFI Said : 4 epouses entre 1989 et 2000, dont une union dissoute (exemple de l'enonce)
     * - TAZI Youssef : 4 epouses aussi, mais la premiere en 1980, avant la periode 1985-2005
     * - Karima ALAMI et Nora HAJJI ont ete mariees deux fois
     * Reutilise par les tests JUnit.
     */
    public static class JeuDeDonnees {
        public Province rabat;
        public Homme safi, tazi;
        public Femme salima, karima;

        public static JeuDeDonnees charger() {
            JeuDeDonnees d = new JeuDeDonnees();
            HommeService hommeService = new HommeService();
            FemmeService femmeService = new FemmeService();
            MariageService mariageService = new MariageService();
            ProvinceService provinceService = new ProvinceService();

            d.rabat = new Province("Rabat", "Rabat-Salé-Kénitra");
            Province casa = new Province("Casablanca", "Casablanca-Settat");
            Province fes = new Province("Fès", "Fès-Meknès");
            for (Province p : new Province[]{d.rabat, casa, fes}) {
                provinceService.create(p);
            }

            d.safi = new Homme("SAFI", "Said", "0661111111", "Agdal", LocalDate.of(1965, 4, 12), d.rabat);
            Homme bennani = new Homme("BENNANI", "Omar", "0662222222", "Batha", LocalDate.of(1962, 8, 3), fes);
            d.tazi = new Homme("TAZI", "Youssef", "0663333333", "Maârif", LocalDate.of(1955, 1, 20), casa);
            Homme fassi = new Homme("FASSI", "Hamza", "0664444444", "Saïss", LocalDate.of(1980, 11, 9), fes);
            Homme kabbaj = new Homme("KABBAJ", "Mehdi", "0665555555", "Anfa", LocalDate.of(1950, 6, 30), casa);
            for (Homme h : new Homme[]{d.safi, bennani, d.tazi, fassi, kabbaj}) {
                hommeService.create(h);
            }

            d.salima = new Femme("RAMI", "Salima", "0671000001", "Agdal", LocalDate.of(1968, 2, 14), d.rabat);
            Femme amal = new Femme("ALI", "Amal", "0671000002", "Hay Riad", LocalDate.of(1972, 7, 1), d.rabat);
            Femme wafa = new Femme("ALAOUI", "Wafa", "0671000003", "Souissi", LocalDate.of(1976, 3, 22), d.rabat);
            d.karima = new Femme("ALAMI", "Karima", "0671000004", "Batha", LocalDate.of(1967, 10, 5), fes);
            Femme nora = new Femme("HAJJI", "Nora", "0671000005", "Zouagha", LocalDate.of(1970, 12, 18), fes);
            Femme leila = new Femme("SBAI", "Leila", "0671000006", "Maârif", LocalDate.of(1958, 5, 9), casa);
            Femme houda = new Femme("NACIRI", "Houda", "0671000007", "Bourgogne", LocalDate.of(1974, 9, 27), casa);
            Femme sanaa = new Femme("CHERKAOUI", "Sanaa", "0671000008", "Oasis", LocalDate.of(1979, 1, 11), casa);
            Femme imane = new Femme("ZIANI", "Imane", "0671000009", "Saïss", LocalDate.of(1985, 4, 4), fes);
            Femme fatima = new Femme("BERRADA", "Fatima", "0671000010", "Anfa", LocalDate.of(1950, 2, 2), casa);
            for (Femme f : new Femme[]{d.salima, amal, wafa, d.karima, nora, leila, houda, sanaa, imane, fatima}) {
                femmeService.create(f);
            }

            Mariage safiSalima = new Mariage(d.safi, d.salima, LocalDate.of(1990, 9, 3), null, 4);
            Mariage[] unions = {
                    new Mariage(d.safi, d.karima, LocalDate.of(1989, 9, 3), LocalDate.of(1990, 9, 3), 0),
                    safiSalima,
                    new Mariage(d.safi, amal, LocalDate.of(1995, 9, 3), null, 2),
                    new Mariage(d.safi, wafa, LocalDate.of(2000, 11, 4), null, 3),
                    new Mariage(bennani, d.karima, LocalDate.of(1992, 5, 10), null, 2),
                    new Mariage(bennani, nora, LocalDate.of(1993, 1, 1), LocalDate.of(2001, 1, 1), 1),
                    new Mariage(d.tazi, leila, LocalDate.of(1980, 1, 1), null, 3),
                    new Mariage(d.tazi, houda, LocalDate.of(1999, 3, 3), null, 2),
                    new Mariage(d.tazi, nora, LocalDate.of(2003, 6, 15), null, 1),
                    new Mariage(d.tazi, sanaa, LocalDate.of(2004, 4, 4), null, 1),
                    new Mariage(fassi, imane, LocalDate.of(2010, 7, 7), null, 0),
                    new Mariage(kabbaj, fatima, LocalDate.of(1975, 2, 2), LocalDate.of(1985, 2, 2), 5)
            };
            for (Mariage m : unions) {
                mariageService.create(m);
            }

            // fiches detaillees pour les 4 enfants de l'union SAFI / RAMI
            EnfantService enfantService = new EnfantService();
            enfantService.create(new Enfant("Yassine", Enfant.Sexe.MASCULIN, LocalDate.of(1991, 6, 2), safiSalima));
            enfantService.create(new Enfant("Sara", Enfant.Sexe.FEMININ, LocalDate.of(1993, 1, 15), safiSalima));
            enfantService.create(new Enfant("Adam", Enfant.Sexe.MASCULIN, LocalDate.of(1996, 9, 30), safiSalima));
            enfantService.create(new Enfant("Lina", Enfant.Sexe.FEMININ, LocalDate.of(2001, 4, 8), safiSalima));
            return d;
        }
    }
}
