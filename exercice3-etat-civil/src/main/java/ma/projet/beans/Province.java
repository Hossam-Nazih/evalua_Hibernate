package ma.projet.beans;

import javax.persistence.*;

/**
 * Nouvelle entite : province de residence des citoyens (l'application gere l'etat civil d'une province).
 */
@Entity
@Table(name = "province")
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom_province", nullable = false, unique = true, length = 60)
    private String nomProvince;

    @Column(name = "region", length = 80)
    private String region;

    public Province() {}

    public Province(String nomProvince, String region) {
        this.nomProvince = nomProvince;
        this.region = region;
    }

    public int getId() { return id; }
    public String getNomProvince() { return nomProvince; }
    public void setNomProvince(String nomProvince) { this.nomProvince = nomProvince; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    @Override
    public String toString() {
        return "Province{id=" + id + ", nomProvince='" + nomProvince + "', region='" + region + "'}";
    }
}
