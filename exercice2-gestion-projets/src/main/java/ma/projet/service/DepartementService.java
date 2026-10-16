package ma.projet.service;

import ma.projet.classes.Departement;
import ma.projet.classes.Employe;
import ma.projet.dao.AbstractDao;

import java.util.List;

/**
 * Service de la nouvelle entite Departement.
 */
public class DepartementService extends AbstractDao<Departement> {

    public DepartementService() {
        super(Departement.class);
    }

    public List<Employe> findMembres(Departement departement) {
        return consulter(session -> session.createQuery(
                        "SELECT e FROM Employe e WHERE e.departement.id = :idDepartement ORDER BY e.nomEmploye", Employe.class)
                .setParameter("idDepartement", departement.getId())
                .list());
    }

    public void afficherMembres(Departement departement) {
        System.out.println("Département " + departement.getCodeDepartement() + " - " + departement.getNomDepartement() + " :");
        for (Employe e : findMembres(departement)) {
            System.out.println("  " + e.getPrenomEmploye() + " " + e.getNomEmploye() + " (" + e.getNumeroTelephone() + ")");
        }
    }
}
