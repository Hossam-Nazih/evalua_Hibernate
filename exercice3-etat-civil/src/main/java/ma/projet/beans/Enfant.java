package ma.projet.beans;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Nouvelle entite : enfant inscrit a l'etat civil, rattache a l'union de ses parents.
 */
@Entity
@Table(name = "enfant")
public class Enfant {

    public enum Sexe { MASCULIN, FEMININ }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "prenom_enfant", nullable = false, length = 50)
    private String prenomEnfant;

    // STRING : la base contient "MASCULIN"/"FEMININ", lisible et insensible a l'ordre des constantes
    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false, length = 10)
    private Sexe sexe;

    @Column(name = "date_naissance_enfant", nullable = false)
    private LocalDate dateNaissanceEnfant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mariage_id", nullable = false)
    private Mariage mariage;

    public Enfant() {}

    public Enfant(String prenomEnfant, Sexe sexe, LocalDate dateNaissanceEnfant, Mariage mariage) {
        this.prenomEnfant = prenomEnfant;
        this.sexe = sexe;
        this.dateNaissanceEnfant = dateNaissanceEnfant;
        this.mariage = mariage;
    }

    public int getId() { return id; }
    public String getPrenomEnfant() { return prenomEnfant; }
    public Sexe getSexe() { return sexe; }
    public LocalDate getDateNaissanceEnfant() { return dateNaissanceEnfant; }
    public Mariage getMariage() { return mariage; }

    @Override
    public String toString() {
        return "Enfant{id=" + id + ", prenomEnfant='" + prenomEnfant + "', sexe=" + sexe
                + ", dateNaissanceEnfant=" + dateNaissanceEnfant + "}";
    }
}
