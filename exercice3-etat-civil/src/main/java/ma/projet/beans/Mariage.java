package ma.projet.beans;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Union entre un epoux et une epouse.
 * Sans date de divorce l'union est en cours ; avec une date de divorce elle est classee "echouee".
 * L'identifiant est independant du couple : les memes personnes peuvent s'unir a nouveau.
 */
@Entity
@Table(name = "mariage")
public class Mariage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date_union", nullable = false)
    private LocalDate dateUnion;

    @Column(name = "date_divorce")
    private LocalDate dateDivorce;

    // nombre declare a l'etat civil (utilise par la requete native de l'enonce)
    @Column(name = "nombre_enfants", nullable = false)
    private int nombreEnfants;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "epoux_id", nullable = false)
    private Homme epoux;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "epouse_id", nullable = false)
    private Femme epouse;

    // nouveau lien : fiche detaillee de chaque enfant ne de l'union
    @OneToMany(mappedBy = "mariage")
    private List<Enfant> enfants = new ArrayList<>();

    public Mariage() {}

    public Mariage(Homme epoux, Femme epouse, LocalDate dateUnion, LocalDate dateDivorce, int nombreEnfants) {
        this.epoux = epoux;
        this.epouse = epouse;
        this.dateUnion = dateUnion;
        this.dateDivorce = dateDivorce;
        this.nombreEnfants = nombreEnfants;
    }

    public boolean estEnCours() {
        return dateDivorce == null;
    }

    public int getId() { return id; }
    public LocalDate getDateUnion() { return dateUnion; }
    public void setDateUnion(LocalDate dateUnion) { this.dateUnion = dateUnion; }
    public LocalDate getDateDivorce() { return dateDivorce; }
    public void setDateDivorce(LocalDate dateDivorce) { this.dateDivorce = dateDivorce; }
    public int getNombreEnfants() { return nombreEnfants; }
    public void setNombreEnfants(int nombreEnfants) { this.nombreEnfants = nombreEnfants; }
    public Homme getEpoux() { return epoux; }
    public Femme getEpouse() { return epouse; }
    public List<Enfant> getEnfants() { return enfants; }
}
