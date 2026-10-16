package ma.projet.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Presentation des valeurs dans les listings console.
 */
public class Formats {
    private static final DateTimeFormatter JOUR_MOIS_ANNEE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
    private static final DecimalFormat SANS_DECIMALES_INUTILES = new DecimalFormat("0.##");

    private Formats() {}

    // "14 mars 2013" devient "14 Mars 2013", comme sur le modele de l'enonce
    public static String dateLongue(LocalDate date) {
        String texte = date.format(JOUR_MOIS_ANNEE);
        int debutMois = texte.indexOf(' ') + 1;
        return texte.substring(0, debutMois) + Character.toUpperCase(texte.charAt(debutMois)) + texte.substring(debutMois + 1);
    }

    // 120.0 -> "120 DH", 99.5 -> "99.5 DH" (point decimal quelle que soit la langue du systeme)
    public static String prix(double montant) {
        return SANS_DECIMALES_INUTILES.format(montant).replace(',', '.') + " DH";
    }
}
