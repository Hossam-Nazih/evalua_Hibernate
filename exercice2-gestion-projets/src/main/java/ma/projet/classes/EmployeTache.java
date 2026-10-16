package ma.projet.classes;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Realisation d'une tache par un employe, avec les dates reellement constatees.
 */
@Entity
@Table(name = "employe_tache")
public class EmployeTache {
    @EmbeddedId
    private EmployeTachePK id;

    // colonnes deja renseignees par la cle composee : associations en lecture seule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", insertable = false, updatable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tache_id", insertable = false, updatable = false)
    private Tache tache;

    @Column(name = "date_debut_reelle")
    private LocalDate dateDebutReelle;

    @Column(name = "date_fin_reelle")
    private LocalDate dateFinReelle;

    public EmployeTache() {}

    // l'employe et la tache doivent deja etre enregistres
    public EmployeTache(Employe employe, Tache tache, LocalDate dateDebutReelle, LocalDate dateFinReelle) {
        this.id = new EmployeTachePK(employe.getId(), tache.getId());
        this.employe = employe;
        this.tache = tache;
        this.dateDebutReelle = dateDebutReelle;
        this.dateFinReelle = dateFinReelle;
    }

    public EmployeTachePK getId() { return id; }
    public Employe getEmploye() { return employe; }
    public Tache getTache() { return tache; }
    public LocalDate getDateDebutReelle() { return dateDebutReelle; }
    public void setDateDebutReelle(LocalDate dateDebutReelle) { this.dateDebutReelle = dateDebutReelle; }
    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }
}
