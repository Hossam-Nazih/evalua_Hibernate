package ma.projet.beans;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Citoyen de sexe masculin ; peut avoir plusieurs unions, simultanees ou successives.
 */
@Entity
@Table(name = "homme")
public class Homme extends Personne {

    // historique complet : unions en cours et dissoutes
    @OneToMany(mappedBy = "epoux")
    private List<Mariage> unions = new ArrayList<>();

    public Homme() {}

    public Homme(String nomFamille, String prenomUsuel, String numeroTelephone, String adresseDomicile,
                 LocalDate dateDeNaissance, Province province) {
        super(nomFamille, prenomUsuel, numeroTelephone, adresseDomicile, dateDeNaissance, province);
    }

    public List<Mariage> getUnions() { return unions; }
}
