package ma.projet.classes;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Etape d'un projet. debutPrevu / finPrevue = planning ; les dates effectives sont dans EmployeTache.
 */
@Entity
@Table(name = "tache")
// requete nommee de l'enonce : taches dont le cout depasse un montant, triees par cout
@NamedQuery(name = "Tache.coutSuperieurA",
        query = "SELECT t FROM Tache t WHERE t.cout > :montant ORDER BY t.cout")
public class Tache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "debut_prevu")
    private LocalDate debutPrevu;

    @Column(name = "fin_prevue")
    private LocalDate finPrevue;

    // montant facture pour la tache, en dirhams
    @Column(name = "cout")
    private double cout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    @OneToMany(mappedBy = "tache")
    private List<EmployeTache> affectations = new ArrayList<>();

    public Tache() {}

    public Tache(String libelle, LocalDate debutPrevu, LocalDate finPrevue, double cout, Projet projet) {
        this.libelle = libelle;
        this.debutPrevu = debutPrevu;
        this.finPrevue = finPrevue;
        this.cout = cout;
        this.projet = projet;
    }

    public int getId() { return id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public LocalDate getDebutPrevu() { return debutPrevu; }
    public void setDebutPrevu(LocalDate debutPrevu) { this.debutPrevu = debutPrevu; }
    public LocalDate getFinPrevue() { return finPrevue; }
    public void setFinPrevue(LocalDate finPrevue) { this.finPrevue = finPrevue; }
    public double getCout() { return cout; }
    public void setCout(double cout) { this.cout = cout; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
    public List<EmployeTache> getAffectations() { return affectations; }

    @Override
    public String toString() {
        return "Tache{id=" + id + ", libelle='" + libelle + "', cout=" + cout + "}";
    }
}
