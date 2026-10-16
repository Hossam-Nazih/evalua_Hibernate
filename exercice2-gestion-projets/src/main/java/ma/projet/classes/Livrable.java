package ma.projet.classes;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Nouvelle entite : document ou version remis au client a une echeance donnee (cahier des charges, maquette...).
 */
@Entity
@Table(name = "livrable")
public class Livrable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "titre", nullable = false, length = 120)
    private String titre;

    // echeance contractuelle
    @Column(name = "date_remise", nullable = false)
    private LocalDate dateRemise;

    // passe a true quand le client a accepte le livrable
    @Column(name = "valide", nullable = false)
    private boolean valide;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    public Livrable() {}

    public Livrable(String titre, LocalDate dateRemise, boolean valide, Projet projet) {
        this.titre = titre;
        this.dateRemise = dateRemise;
        this.valide = valide;
        this.projet = projet;
    }

    public int getId() { return id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public LocalDate getDateRemise() { return dateRemise; }
    public void setDateRemise(LocalDate dateRemise) { this.dateRemise = dateRemise; }
    public boolean isValide() { return valide; }
    public void setValide(boolean valide) { this.valide = valide; }
    public Projet getProjet() { return projet; }

    @Override
    public String toString() {
        return "Livrable{id=" + id + ", titre='" + titre + "', dateRemise=" + dateRemise + ", valide=" + valide + "}";
    }
}
