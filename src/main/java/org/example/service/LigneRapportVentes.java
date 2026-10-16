package org.example.service;

import java.math.BigDecimal;

/**
 * Une ligne du rapport mensuel : chiffre d'affaires d'une categorie.
 */
public class LigneRapportVentes {
    private final String categorie;
    private final BigDecimal chiffreAffaires;

    public LigneRapportVentes(String categorie, BigDecimal chiffreAffaires) {
        this.categorie = categorie;
        this.chiffreAffaires = chiffreAffaires;
    }

    public String getCategorie() { return categorie; }
    public BigDecimal getChiffreAffaires() { return chiffreAffaires; }

    @Override
    public String toString() {
        return categorie + " : " + chiffreAffaires;
    }
}
