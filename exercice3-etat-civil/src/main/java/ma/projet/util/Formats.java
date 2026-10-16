package ma.projet.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Mise en forme des dates pour les affichages console.
 */
public class Formats {
    private static final DateTimeFormatter DATE_COURTE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formats() {}

    // 03/09/1990 ; une date absente s'affiche par un tiret
    public static String dateCourte(LocalDate date) {
        return date == null ? "-" : date.format(DATE_COURTE);
    }
}
