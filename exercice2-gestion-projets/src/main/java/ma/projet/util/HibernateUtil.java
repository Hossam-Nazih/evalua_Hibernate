package ma.projet.util;

import ma.projet.classes.Departement;
import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Livrable;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Fabrique de sessions Hibernate, creee au chargement de la classe et partagee par tous les services.
 * Les parametres de connexion viennent de application.properties ; les entites sont enregistrees une a une.
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory = initialiser();

    private HibernateUtil() {}

    private static SessionFactory initialiser() {
        try (InputStream flux = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (flux == null) {
                throw new IllegalStateException("Fichier application.properties absent du classpath");
            }
            Properties parametres = new Properties();
            parametres.load(flux);
            return new Configuration()
                    .addProperties(parametres)
                    .addAnnotatedClass(Departement.class)
                    .addAnnotatedClass(Employe.class)
                    .addAnnotatedClass(Projet.class)
                    .addAnnotatedClass(Tache.class)
                    .addAnnotatedClass(EmployeTache.class)
                    .addAnnotatedClass(Livrable.class)
                    .buildSessionFactory();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // ferme le pool de connexions ; a appeler en fin de programme
    public static void shutdown() {
        sessionFactory.close();
    }
}
