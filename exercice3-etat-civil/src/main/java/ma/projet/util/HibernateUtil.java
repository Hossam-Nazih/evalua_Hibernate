package ma.projet.util;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Construit une seule SessionFactory pour toute l'application,
 * a partir de application.properties et des classes entites declarees ici.
 * Avec hbm2ddl.auto=update, les tables homme, femme et mariage sont generees au premier demarrage.
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory = construire();

    private HibernateUtil() {}

    private static SessionFactory construire() {
        try (InputStream fichier = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (fichier == null) {
                throw new IllegalStateException("application.properties introuvable dans le classpath");
            }
            Properties proprietes = new Properties();
            proprietes.load(fichier);
            return new Configuration()
                    .addProperties(proprietes)
                    .addAnnotatedClass(Homme.class)
                    .addAnnotatedClass(Femme.class)
                    .addAnnotatedClass(Mariage.class)
                    .buildSessionFactory();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        sessionFactory.close();
    }
}
