package ma.projet.beans;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "femme")
// requete SQL native : total des enfants issus des mariages commences entre deux dates
@NamedNativeQuery(name = "Femme.nombreEnfantsEntre",
        query = "SELECT COALESCE(SUM(m.nbr_enfant), 0) FROM mariage m "
              + "WHERE m.femme_id = :femmeId AND m.date_debut BETWEEN :debut AND :fin")
// requete JPQL : femmes qui apparaissent dans au moins deux mariages
@NamedQuery(name = "Femme.marieesAuMoinsDeuxFois",
        query = "SELECT f FROM Femme f WHERE (SELECT COUNT(m) FROM Mariage m WHERE m.femme = f) >= 2 ORDER BY f.nom")
public class Femme extends Personne {

    @OneToMany(mappedBy = "femme")
    private List<Mariage> mariages = new ArrayList<>();

    public Femme() {}

    public Femme(String nom, String prenom, String telephone, String adresse, LocalDate dateNaissance) {
        super(nom, prenom, telephone, adresse, dateNaissance);
    }

    public List<Mariage> getMariages() { return mariages; }
}
