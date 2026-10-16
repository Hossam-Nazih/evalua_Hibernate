package ma.projet.beans;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Mariage entre un homme et une femme.
 * dateFin vide : mariage en cours ; dateFin renseignee : mariage termine (echoue).
 * Un identifiant propre (et non le couple homme/femme) permet a deux personnes de se remarier.
 */
@Entity
@Table(name = "mariage")
public class Mariage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "nbr_enfant", nullable = false)
    private int nbrEnfant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "homme_id", nullable = false)
    private Homme homme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "femme_id", nullable = false)
    private Femme femme;

    public Mariage() {}

    public Mariage(Homme homme, Femme femme, LocalDate dateDebut, LocalDate dateFin, int nbrEnfant) {
        this.homme = homme;
        this.femme = femme;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nbrEnfant = nbrEnfant;
    }

    public boolean estEnCours() {
        return dateFin == null;
    }

    public int getId() { return id; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public int getNbrEnfant() { return nbrEnfant; }
    public void setNbrEnfant(int nbrEnfant) { this.nbrEnfant = nbrEnfant; }
    public Homme getHomme() { return homme; }
    public Femme getFemme() { return femme; }
}
