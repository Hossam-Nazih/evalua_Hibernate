package ma.projet.beans;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Citoyenne ; porte les deux requetes nommees demandees par l'enonce.
 */
@Entity
@Table(name = "femme")
// SQL natif : enfants nes des unions de la femme commencees dans l'intervalle (0 si aucune)
@NamedNativeQuery(name = "Femme.enfantsEntreDeuxDates",
        query = "SELECT COALESCE(SUM(m.nombre_enfants), 0) FROM mariage m "
              + "WHERE m.epouse_id = :idFemme AND m.date_union BETWEEN :du AND :au")
// JPQL : la sous-requete compte les unions de chaque femme
@NamedQuery(name = "Femme.marieesDeuxFoisOuPlus",
        query = "SELECT f FROM Femme f WHERE (SELECT COUNT(m) FROM Mariage m WHERE m.epouse = f) >= 2 ORDER BY f.nomFamille")
public class Femme extends Personne {

    @OneToMany(mappedBy = "epouse")
    private List<Mariage> unions = new ArrayList<>();

    public Femme() {}

    public Femme(String nomFamille, String prenomUsuel, String numeroTelephone, String adresseDomicile,
                 LocalDate dateDeNaissance, Province province) {
        super(nomFamille, prenomUsuel, numeroTelephone, adresseDomicile, dateDeNaissance, province);
    }

    public List<Mariage> getUnions() { return unions; }
}
