package ma.projet.classes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ingenieur ou technicien du bureau. Il peut piloter des projets et se voir affecter des taches.
 */
@Entity
@Table(name = "employe")
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom_employe", nullable = false, length = 50)
    private String nomEmploye;

    @Column(name = "prenom_employe", nullable = false, length = 50)
    private String prenomEmploye;

    @Column(name = "numero_telephone", length = 20)
    private String numeroTelephone;

    // nouveau lien : departement de rattachement (facultatif)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    private Departement departement;

    // projets pour lesquels il est chef (cote proprietaire : Projet.chefProjet)
    @OneToMany(mappedBy = "chefProjet")
    private List<Projet> projetsDiriges = new ArrayList<>();

    // taches sur lesquelles il a travaille, avec les dates effectives
    @OneToMany(mappedBy = "employe")
    private List<EmployeTache> affectations = new ArrayList<>();

    public Employe() {}

    public Employe(String nomEmploye, String prenomEmploye, String numeroTelephone) {
        this.nomEmploye = nomEmploye;
        this.prenomEmploye = prenomEmploye;
        this.numeroTelephone = numeroTelephone;
    }

    public Employe(String nomEmploye, String prenomEmploye, String numeroTelephone, Departement departement) {
        this(nomEmploye, prenomEmploye, numeroTelephone);
        this.departement = departement;
    }

    public int getId() { return id; }
    public String getNomEmploye() { return nomEmploye; }
    public void setNomEmploye(String nomEmploye) { this.nomEmploye = nomEmploye; }
    public String getPrenomEmploye() { return prenomEmploye; }
    public void setPrenomEmploye(String prenomEmploye) { this.prenomEmploye = prenomEmploye; }
    public String getNumeroTelephone() { return numeroTelephone; }
    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }
    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) { this.departement = departement; }
    public List<Projet> getProjetsDiriges() { return projetsDiriges; }
    public List<EmployeTache> getAffectations() { return affectations; }

    @Override
    public String toString() {
        return "Employe{id=" + id + ", nomEmploye='" + nomEmploye + "', prenomEmploye='" + prenomEmploye + "'}";
    }
}
