package ma.projet.classes;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tache d'un projet. dateDebut / dateFin sont les dates planifiees ;
 * les dates reelles sont saisies dans EmployeTache.
 */
@Entity
@Table(name = "tache")
// requete nommee demandee par l'enonce : taches dont le prix depasse un montant
@NamedQuery(name = "Tache.prixSuperieur", query = "SELECT t FROM Tache t WHERE t.prix > :prix ORDER BY t.prix")
public class Tache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    // cout de la tache en DH
    private double prix;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    @OneToMany(mappedBy = "tache")
    private List<EmployeTache> employeTaches = new ArrayList<>();

    public Tache() {}

    public Tache(String nom, LocalDate dateDebut, LocalDate dateFin, double prix, Projet projet) {
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prix = prix;
        this.projet = projet;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
    public List<EmployeTache> getEmployeTaches() { return employeTaches; }

    @Override
    public String toString() {
        return "Tache{id=" + id + ", nom='" + nom + "', prix=" + prix + "}";
    }
}
