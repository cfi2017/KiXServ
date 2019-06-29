package dev.swiss.kix;

import javax.persistence.EntityManagerFactory;

public class Persistence {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = javax.persistence.Persistence.createEntityManagerFactory("server01");
        }
        return emf;
    }

}
