package ma.projet.util;

import ma.projet.beans.Enfant;
import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.beans.Province;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Fabrique de sessions Hibernate, creee au chargement de la classe et partagee par tous les services.
 * Avec hbm2ddl.auto=update, les tables province, homme, femme, mariage et enfant sont generees au demarrage.
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
                    .addAnnotatedClass(Province.class)
                    .addAnnotatedClass(Homme.class)
                    .addAnnotatedClass(Femme.class)
                    .addAnnotatedClass(Mariage.class)
                    .addAnnotatedClass(Enfant.class)
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
