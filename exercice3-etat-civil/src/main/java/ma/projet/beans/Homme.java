package ma.projet.beans;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "homme")
public class Homme extends Personne {

    // tous ses mariages, en cours ou termines
    @OneToMany(mappedBy = "homme")
    private List<Mariage> mariages = new ArrayList<>();

    public Homme() {}

    public Homme(String nom, String prenom, String telephone, String adresse, LocalDate dateNaissance) {
        super(nom, prenom, telephone, adresse, dateNaissance);
    }

    public List<Mariage> getMariages() { return mariages; }
}
