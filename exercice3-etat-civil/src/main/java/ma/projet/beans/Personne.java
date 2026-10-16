package ma.projet.beans;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Identite civile commune a Homme et Femme.
 * @MappedSuperclass n'a pas de table : ses colonnes sont dupliquees dans les tables homme et femme,
 * chacune avec sa propre sequence d'identifiants.
 */
@MappedSuperclass
public abstract class Personne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom_famille", nullable = false, length = 50)
    private String nomFamille;

    @Column(name = "prenom_usuel", nullable = false, length = 50)
    private String prenomUsuel;

    @Column(name = "numero_telephone", length = 20)
    private String numeroTelephone;

    @Column(name = "adresse_domicile", length = 150)
    private String adresseDomicile;

    @Column(name = "date_de_naissance")
    private LocalDate dateDeNaissance;

    // nouveau lien : province de residence (facultative)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;

    protected Personne() {}

    protected Personne(String nomFamille, String prenomUsuel, String numeroTelephone, String adresseDomicile,
                       LocalDate dateDeNaissance, Province province) {
        this.nomFamille = nomFamille;
        this.prenomUsuel = prenomUsuel;
        this.numeroTelephone = numeroTelephone;
        this.adresseDomicile = adresseDomicile;
        this.dateDeNaissance = dateDeNaissance;
        this.province = province;
    }

    public int getId() { return id; }
    public String getNomFamille() { return nomFamille; }
    public void setNomFamille(String nomFamille) { this.nomFamille = nomFamille; }
    public String getPrenomUsuel() { return prenomUsuel; }
    public void setPrenomUsuel(String prenomUsuel) { this.prenomUsuel = prenomUsuel; }
    public String getNumeroTelephone() { return numeroTelephone; }
    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }
    public String getAdresseDomicile() { return adresseDomicile; }
    public void setAdresseDomicile(String adresseDomicile) { this.adresseDomicile = adresseDomicile; }
    public LocalDate getDateDeNaissance() { return dateDeNaissance; }
    public void setDateDeNaissance(LocalDate dateDeNaissance) { this.dateDeNaissance = dateDeNaissance; }
    public Province getProvince() { return province; }
    public void setProvince(Province province) { this.province = province; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", nomFamille='" + nomFamille + "', prenomUsuel='" + prenomUsuel
                + "', dateDeNaissance=" + dateDeNaissance + "}";
    }
}
