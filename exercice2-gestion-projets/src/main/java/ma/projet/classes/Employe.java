package ma.projet.classes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Employe du bureau d'etudes : il peut diriger des projets (chef) et realiser des taches.
 */
@Entity
@Table(name = "employe")
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(length = 20)
    private String telephone;

    // projets dont il est le chef (cle etrangere chef_id dans la table projet)
    @OneToMany(mappedBy = "chef")
    private List<Projet> projetsGeres = new ArrayList<>();

    // ses affectations aux taches, avec les dates reelles
    @OneToMany(mappedBy = "employe")
    private List<EmployeTache> employeTaches = new ArrayList<>();

    public Employe() {}

    public Employe(String nom, String prenom, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public List<Projet> getProjetsGeres() { return projetsGeres; }
    public List<EmployeTache> getEmployeTaches() { return employeTaches; }

    @Override
    public String toString() {
        return "Employe{id=" + id + ", nom='" + nom + "', prenom='" + prenom + "'}";
    }
}
