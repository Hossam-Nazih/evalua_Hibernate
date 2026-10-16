package ma.projet.classes;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Affectation d'un employe a une tache, avec les dates auxquelles le travail a vraiment eu lieu.
 */
@Entity
@Table(name = "employe_tache")
public class EmployeTache {
    @EmbeddedId
    private EmployeTachePK id;

    // les deux colonnes de jointure appartiennent a la cle : pas d'ecriture par ces associations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", insertable = false, updatable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tache_id", insertable = false, updatable = false)
    private Tache tache;

    @Column(name = "debut_effectif")
    private LocalDate debutEffectif;

    @Column(name = "fin_effective")
    private LocalDate finEffective;

    public EmployeTache() {}

    // employe et tache deja persistes : leurs identifiants forment la cle
    public EmployeTache(Employe employe, Tache tache, LocalDate debutEffectif, LocalDate finEffective) {
        this.id = new EmployeTachePK(employe.getId(), tache.getId());
        this.employe = employe;
        this.tache = tache;
        this.debutEffectif = debutEffectif;
        this.finEffective = finEffective;
    }

    public EmployeTachePK getId() { return id; }
    public Employe getEmploye() { return employe; }
    public Tache getTache() { return tache; }
    public LocalDate getDebutEffectif() { return debutEffectif; }
    public void setDebutEffectif(LocalDate debutEffectif) { this.debutEffectif = debutEffectif; }
    public LocalDate getFinEffective() { return finEffective; }
    public void setFinEffective(LocalDate finEffective) { this.finEffective = finEffective; }
}
