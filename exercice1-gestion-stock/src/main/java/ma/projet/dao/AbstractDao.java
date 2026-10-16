package ma.projet.dao;

import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation de IDao partagee par les services : une session par operation,
 * toujours refermee, et une transaction annulee si l'ecriture echoue.
 */
public abstract class AbstractDao<T> implements IDao<T> {
    private final Class<T> type;

    protected AbstractDao(Class<T> type) {
        this.type = type;
    }

    @Override
    public boolean create(T o) {
        return ecrire(session -> session.save(o));
    }

    @Override
    public boolean delete(T o) {
        return ecrire(session -> session.delete(o));
    }

    @Override
    public boolean update(T o) {
        return ecrire(session -> session.update(o));
    }

    @Override
    public T findById(int id) {
        return lire(session -> session.get(type, id));
    }

    @Override
    public List<T> findAll() {
        return lire(session -> session.createQuery("from " + type.getSimpleName(), type).list());
    }

    // requete en lecture : pas besoin de transaction
    protected <R> R lire(Function<Session, R> requete) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return requete.apply(session);
        }
    }

    protected boolean ecrire(Consumer<Session> action) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            action.accept(session);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("Operation annulee sur " + type.getSimpleName() + " : " + e.getMessage());
            return false;
        }
    }
}
