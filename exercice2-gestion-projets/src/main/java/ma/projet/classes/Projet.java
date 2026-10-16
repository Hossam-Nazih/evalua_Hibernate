package ma.projet.classes;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Mission confiee au bureau d'etudes : pilotee par un chef, decoupee en taches, conclue par des livrables.
 */
@Entity
@Table(name = "projet")
public class Projet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "intitule", nullable = false, length = 100)
    private String intitule;

    @Column(name = "date_lancement")
    private LocalDate dateLancement;

    @Column(name = "date_cloture")
    private LocalDate dateCloture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chef_projet_id")
    private Employe chefProjet;

    @OneToMany(mappedBy = "projet")
    private List<Tache> listeTaches = new ArrayList<>();

    @OneToMany(mappedBy = "projet")
    private List<Livrable> livrables = new ArrayList<>();

    public Projet() {}

    public Projet(String intitule, LocalDate dateLancement, LocalDate dateCloture, Employe chefProjet) {
        this.intitule = intitule;
        this.dateLancement = dateLancement;
        this.dateCloture = dateCloture;
        this.chefProjet = chefProjet;
    }

    public int getId() { return id; }
    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public LocalDate getDateLancement() { return dateLancement; }
    public void setDateLancement(LocalDate dateLancement) { this.dateLancement = dateLancement; }
    public LocalDate getDateCloture() { return dateCloture; }
    public void setDateCloture(LocalDate dateCloture) { this.dateCloture = dateCloture; }
    public Employe getChefProjet() { return chefProjet; }
    public void setChefProjet(Employe chefProjet) { this.chefProjet = chefProjet; }
    public List<Tache> getListeTaches() { return listeTaches; }
    public List<Livrable> getLivrables() { return livrables; }

    @Override
    public String toString() {
        return "Projet{id=" + id + ", intitule='" + intitule + "', dateLancement=" + dateLancement
                + ", dateCloture=" + dateCloture + "}";
    }
}
