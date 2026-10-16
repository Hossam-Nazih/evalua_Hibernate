package ma.projet.classes;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Cle composee d'une affectation : (employe, tache).
 */
@Embeddable
public class EmployeTachePK implements Serializable {
    @Column(name = "employe_id")
    private int employeId;

    @Column(name = "tache_id")
    private int tacheId;

    public EmployeTachePK() {}

    public EmployeTachePK(int employeId, int tacheId) {
        this.employeId = employeId;
        this.tacheId = tacheId;
    }

    public int getEmployeId() { return employeId; }
    public int getTacheId() { return tacheId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeTachePK)) return false;
        EmployeTachePK autre = (EmployeTachePK) o;
        return employeId == autre.employeId && tacheId == autre.tacheId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeId, tacheId);
    }
}
