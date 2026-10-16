package ma.projet.dao;

import java.util.List;

/**
 * Contrat CRUD commun a tous les services.
 * Les methodes d'ecriture renvoient false si l'operation a ete annulee.
 */
public interface IDao<T> {
    boolean create(T o);

    boolean delete(T o);

    boolean update(T o);

    T findById(int id);

    List<T> findAll();
}
