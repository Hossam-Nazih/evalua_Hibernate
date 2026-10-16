package org.example.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * EntityManagerFactory unique de l'application (unite defaultPU de persistence.xml),
 * partagee par le GenericDAO et les services.
 */
public class JpaUtil {
    private static EntityManagerFactory emf;

    private JpaUtil() {}

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("defaultPU", surcharges());
        }
        return emf;
    }

    public static synchronized void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }

    // les proprietes systeme javax.persistence.* et hibernate.* remplacent celles de persistence.xml
    // (les tests JUnit s'en servent pour basculer sur H2)
    private static Map<String, String> surcharges() {
        Map<String, String> proprietes = new HashMap<>();
        for (String nom : System.getProperties().stringPropertyNames()) {
            if (nom.startsWith("javax.persistence.") || nom.startsWith("hibernate.")) {
                proprietes.put(nom, System.getProperty(nom));
            }
        }
        return proprietes;
    }
}
