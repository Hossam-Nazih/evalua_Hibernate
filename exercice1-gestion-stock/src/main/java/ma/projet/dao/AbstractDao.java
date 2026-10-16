package ma.projet.dao;

import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Socle commun des services : ecrit une seule fois le code Hibernate de IDao.
 * Chaque appel ouvre sa propre Session (fermee par le try-with-resources) ;
 * une ecriture qui echoue est annulee par rollback et signalee par un retour false.
 */
public abstract class AbstractDao<T> implements IDao<T> {
    private final Class<T> classeEntite;

    protected AbstractDao(Class<T> classeEntite) {
        this.classeEntite = classeEntite;
    }

    @Override
    public boolean create(T o) {
        return executerTransaction(session -> session.save(o));
    }

    @Override
    public boolean delete(T o) {
        return executerTransaction(session -> session.delete(o));
    }

    @Override
    public boolean update(T o) {
        return executerTransaction(session -> session.update(o));
    }

    @Override
    public T findById(int id) {
        return consulter(session -> session.get(classeEntite, id));
    }

    @Override
    public List<T> findAll() {
        // HQL : le nom de l'entite est celui de la classe
        return consulter(session -> session.createQuery("from " + classeEntite.getSimpleName() + " order by id", classeEntite).list());
    }

    // lecture seule : la session suffit, aucune transaction n'est ouverte
    protected <R> R consulter(Function<Session, R> requete) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return requete.apply(session);
        }
    }

    protected boolean executerTransaction(Consumer<Session> travail) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            travail.accept(session);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("Echec sur " + classeEntite.getSimpleName() + ", transaction annulee : " + e.getMessage());
            return false;
        }
    }
}
