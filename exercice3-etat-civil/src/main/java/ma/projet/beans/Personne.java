package ma.projet.beans;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Champs communs aux hommes et aux femmes.
 * @MappedSuperclass : pas de table personne, les colonnes sont recopiees dans homme et dans femme.
 */
@MappedSuperclass
public abstract class Personne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(length = 20)
    private String telephone;

    @Column(length = 150)
    private String adresse;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    protected Personne() {}

    protected Personne(String nom, String prenom, String telephone, String adresse, LocalDate dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.adresse = adresse;
        this.dateNaissance = dateNaissance;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", nom='" + nom + "', prenom='" + prenom
                + "', dateNaissance=" + dateNaissance + "}";
    }
}
