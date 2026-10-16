package org.example.service;

import org.example.dao.GenericDAO;

import java.io.Serializable;
import java.util.List;

/**
 * Couche metier generique entre le DAO et les controleurs : centralise les validations.
 */
public class CrudService<T, ID extends Serializable> {
    private final GenericDAO<T, ID> dao;

    public CrudService(Class<T> entityClass) {
        this.dao = new GenericDAO<>(entityClass);
    }

    public T getById(ID id) {
        return dao.find(id);
    }

    public List<T> getAll() {
        return dao.findAll();
    }

    public void create(T entity) {
        verifierNonNulle(entity);
        dao.create(entity);
    }

    public T update(T entity) {
        verifierNonNulle(entity);
        return dao.update(entity);
    }

    public void delete(T entity) {
        verifierNonNulle(entity);
        dao.delete(entity);
    }

    private void verifierNonNulle(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
    }
}
