package ma.projet.classes;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Projet suivi par le bureau d'etudes, dirige par un employe et decoupe en taches.
 */
@Entity
@Table(name = "projet")
public class Projet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chef_id")
    private Employe chef;

    @OneToMany(mappedBy = "projet")
    private List<Tache> taches = new ArrayList<>();

    public Projet() {}

    public Projet(String nom, LocalDate dateDebut, LocalDate dateFin, Employe chef) {
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.chef = chef;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public Employe getChef() { return chef; }
    public void setChef(Employe chef) { this.chef = chef; }
    public List<Tache> getTaches() { return taches; }

    @Override
    public String toString() {
        return "Projet{id=" + id + ", nom='" + nom + "', dateDebut=" + dateDebut + ", dateFin=" + dateFin + "}";
    }
}
