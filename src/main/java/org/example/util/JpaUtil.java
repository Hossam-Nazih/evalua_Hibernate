package org.example.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Point d'acces unique a l'EntityManagerFactory : sa creation est couteuse,
 * on la fait donc une seule fois et tous les DAO/services la reutilisent.
 */
public class JpaUtil {
    private static EntityManagerFactory emf;

    private JpaUtil() {}

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("defaultPU");
        }
        return emf;
    }

    // a appeler en fin de programme ou a l'arret du serveur
    public static synchronized void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }
}
