package org.example.web;

import org.example.service.LigneRapportVentes;
import org.example.service.RapportVentesService;
import org.example.util.ConsoleH2;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Page HTML du rapport de ventes.
 *   /rapport-ventes              mois precedent (par defaut)
 *   /rapport-ventes?mois=AAAA-MM mois choisi
 */
public class RapportVentesServlet extends HttpServlet {
    private RapportVentesService rapportService;

    @Override
    public void init() {
        rapportService = new RapportVentesService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        YearMonth mois;
        String parametre = req.getParameter("mois");
        try {
            mois = parametre == null || parametre.isEmpty() ? YearMonth.now().minusMonths(1) : YearMonth.parse(parametre);
        } catch (DateTimeParseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre mois attendu au format AAAA-MM");
            return;
        }

        List<LigneRapportVentes> rapport = rapportService.rapportMensuel(mois);
        BigDecimal total = rapport.stream()
                .map(LigneRapportVentes::getChiffreAffaires)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html lang=\"fr\"><head><meta charset=\"UTF-8\">");
        out.println("<title>Rapport des ventes " + mois + "</title></head><body>");
        out.println("<h1>Chiffre d'affaires par catégorie - " + mois + "</h1>");
        out.println("<p><a href=\"?mois=" + mois.minusMonths(1) + "\">&larr; " + mois.minusMonths(1) + "</a> | "
                + "<a href=\"?mois=" + mois.plusMonths(1) + "\">" + mois.plusMonths(1) + " &rarr;</a></p>");
        out.println("<table border=\"1\" cellpadding=\"6\"><tr><th>Catégorie</th><th>Chiffre d'affaires</th></tr>");
        for (LigneRapportVentes ligne : rapport) {
            out.println("<tr><td>" + echapperHtml(ligne.getIntituleCategorie()) + "</td><td align=\"right\">"
                    + ligne.getChiffreAffaires() + "</td></tr>");
        }
        out.println("<tr><th>Total</th><th align=\"right\">" + total + "</th></tr></table>");
        out.println("<p><a href=\"" + ConsoleH2.URL + "\" target=\"_blank\">Console H2</a>"
                + " (JDBC URL : jdbc:h2:mem:gestion_commerciale, utilisateur : sa)</p>");
        out.println("</body></html>");
    }

    // un intitule saisi en base ne doit pas pouvoir injecter de balises dans la page
    private static String echapperHtml(String texte) {
        return texte.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
