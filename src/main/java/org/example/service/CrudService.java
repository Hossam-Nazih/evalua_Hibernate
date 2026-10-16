package org.example.service;

import org.example.dao.GenericDAO;

import java.io.Serializable;
import java.util.List;

/**
 * Couche metier commune : controle les donnees recues puis delegue l'acces a la base au DAO.
 * Les services specialises en heritent et ajoutent leurs propres regles.
 */
public class CrudService<T, ID extends Serializable> {
    private final GenericDAO<T, ID> dao;

    public CrudService(Class<T> typeEntite) {
        this.dao = new GenericDAO<>(typeEntite);
    }

    public T getById(ID id) {
        return dao.find(id);
    }

    public List<T> getAll() {
        return dao.findAll();
    }

    public void create(T entite) {
        exigerEntite(entite);
        dao.create(entite);
    }

    public T update(T entite) {
        exigerEntite(entite);
        return dao.update(entite);
    }

    public void delete(T entite) {
        exigerEntite(entite);
        dao.delete(entite);
    }

    // controle partage par toutes les operations d'ecriture
    protected void exigerEntite(T entite) {
        if (entite == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
    }
}
