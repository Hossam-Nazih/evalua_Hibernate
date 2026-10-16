package ma.projet.classes;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Cle double d'une affectation : un employe ne peut etre affecte qu'une fois a une meme tache.
 */
@Embeddable
public class EmployeTachePK implements Serializable {
    @Column(name = "employe_id")
    private int idEmploye;

    @Column(name = "tache_id")
    private int idTache;

    public EmployeTachePK() {}

    public EmployeTachePK(int idEmploye, int idTache) {
        this.idEmploye = idEmploye;
        this.idTache = idTache;
    }

    public int getIdEmploye() { return idEmploye; }
    public int getIdTache() { return idTache; }

    // comparaison par valeur, indispensable pour qu'Hibernate retrouve l'entite dans sa session
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeTachePK)) return false;
        EmployeTachePK cle = (EmployeTachePK) o;
        return idEmploye == cle.idEmploye && idTache == cle.idTache;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEmploye, idTache);
    }
}
