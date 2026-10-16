package ma.projet.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Presentation des dates et des montants dans les listings console.
 */
public class Formats {
    private static final DateTimeFormatter JOUR_MOIS_ANNEE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
    private static final DateTimeFormatter JJ_MM_AAAA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat SANS_DECIMALES_INUTILES = new DecimalFormat("0.##");

    private Formats() {}

    // "14 janvier 2013" devient "14 Janvier 2013", comme sur le modele de l'enonce
    public static String dateLongue(LocalDate date) {
        String texte = date.format(JOUR_MOIS_ANNEE);
        int debutMois = texte.indexOf(' ') + 1;
        return texte.substring(0, debutMois) + Character.toUpperCase(texte.charAt(debutMois)) + texte.substring(debutMois + 1);
    }

    // 10/02/2013 ; "-" quand la date n'est pas encore connue
    public static String dateCourte(LocalDate date) {
        return date == null ? "-" : date.format(JJ_MM_AAAA);
    }

    // 1200.0 -> "1200 DH"
    public static String prix(double montant) {
        return SANS_DECIMALES_INUTILES.format(montant).replace(',', '.') + " DH";
    }
}
