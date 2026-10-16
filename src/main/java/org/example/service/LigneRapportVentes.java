package org.example.service;

import java.math.BigDecimal;

/**
 * Objet de transfert (pas une entite) : une categorie et ses ventes sur la periode.
 */
public class LigneRapportVentes {
    private final String intituleCategorie;
    private final BigDecimal chiffreAffaires;

    public LigneRapportVentes(String intituleCategorie, BigDecimal chiffreAffaires) {
        this.intituleCategorie = intituleCategorie;
        this.chiffreAffaires = chiffreAffaires;
    }

    public String getIntituleCategorie() { return intituleCategorie; }
    public BigDecimal getChiffreAffaires() { return chiffreAffaires; }

    @Override
    public String toString() {
        return intituleCategorie + " : " + chiffreAffaires;
    }
}
