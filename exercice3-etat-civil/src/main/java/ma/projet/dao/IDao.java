package ma.projet.dao;

import java.util.List;

/**
 * Operations de base que chaque service doit fournir pour son entite.
 * create / update / delete renvoient true si la transaction a ete validee, false si elle a ete annulee.
 */
public interface IDao<T> {
    boolean create(T o);

    boolean delete(T o);

    boolean update(T o);

    T findById(int id);

    List<T> findAll();
}
