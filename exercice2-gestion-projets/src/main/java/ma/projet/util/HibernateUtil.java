package ma.projet.util;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Construit une seule SessionFactory pour toute l'application,
 * a partir de application.properties et des classes entites declarees ici.
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
                    .addAnnotatedClass(Employe.class)
                    .addAnnotatedClass(Projet.class)
                    .addAnnotatedClass(Tache.class)
                    .addAnnotatedClass(EmployeTache.class)
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
