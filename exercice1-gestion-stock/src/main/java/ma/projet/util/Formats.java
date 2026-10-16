package ma.projet.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Mise en forme des dates et des prix pour les affichages console.
 */
public class Formats {
    private static final DateTimeFormatter DATE_LONGUE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
    private static final DecimalFormat MONTANT = new DecimalFormat("0.##");

    private Formats() {}

    // 14 Mars 2013 : le mois francais est mis avec une majuscule
    public static String dateLongue(LocalDate date) {
        String texte = date.format(DATE_LONGUE);
        int espace = texte.indexOf(' ');
        return texte.substring(0, espace + 1) + Character.toUpperCase(texte.charAt(espace + 1)) + texte.substring(espace + 2);
    }

    // 120 DH, 99.5 DH
    public static String prix(double montant) {
        return MONTANT.format(montant).replace(',', '.') + " DH";
    }
}
