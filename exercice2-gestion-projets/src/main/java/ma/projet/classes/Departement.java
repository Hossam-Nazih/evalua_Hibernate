package ma.projet.classes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Nouvelle entite : service interne du bureau d'etudes (Etudes, Developpement...)
 * auquel chaque employe est rattache.
 */
@Entity
@Table(name = "departement")
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code_departement", nullable = false, unique = true, length = 10)
    private String codeDepartement;

    @Column(name = "nom_departement", nullable = false, length = 80)
    private String nomDepartement;

    @OneToMany(mappedBy = "departement")
    private List<Employe> membres = new ArrayList<>();

    public Departement() {}

    public Departement(String codeDepartement, String nomDepartement) {
        this.codeDepartement = codeDepartement;
        this.nomDepartement = nomDepartement;
    }

    public int getId() { return id; }
    public String getCodeDepartement() { return codeDepartement; }
    public void setCodeDepartement(String codeDepartement) { this.codeDepartement = codeDepartement; }
    public String getNomDepartement() { return nomDepartement; }
    public void setNomDepartement(String nomDepartement) { this.nomDepartement = nomDepartement; }
    public List<Employe> getMembres() { return membres; }

    @Override
    public String toString() {
        return "Departement{id=" + id + ", codeDepartement='" + codeDepartement + "', nomDepartement='" + nomDepartement + "'}";
    }
}
