package com.example.util;

import java.util.List;

/**
 * Resultat pagine generique : les elements de la page courante
 * plus ce qu'il faut pour afficher une barre de navigation.
 */
public class PaginationResult<T> {

    private final List<T> elements;
    private final int pageCourante;    // numerotee a partir de 1
    private final int taillePage;
    private final int nbPages;
    private final long nbElementsTotal;

    public PaginationResult(List<T> elements, int pageCourante, int taillePage, long nbElementsTotal) {
        this.elements = elements;
        this.pageCourante = pageCourante;
        this.taillePage = taillePage;
        this.nbElementsTotal = nbElementsTotal;
        // division arrondie au superieur : la derniere page peut etre incomplete
        this.nbPages = (int) Math.ceil((double) nbElementsTotal / taillePage);
    }

    public List<T> getElements() { return elements; }
    public int getPageCourante() { return pageCourante; }
    public int getTaillePage() { return taillePage; }
    public int getNbPages() { return nbPages; }
    public long getNbElementsTotal() { return nbElementsTotal; }

    public boolean aUnePageSuivante() { return pageCourante < nbPages; }
    public boolean aUnePagePrecedente() { return pageCourante > 1; }

    @Override
    public String toString() {
        return "Page " + pageCourante + "/" + nbPages + " (" + nbElementsTotal + " salles) : " + elements;
    }
}
