package it.unibas.tav.iotsentinel.persistenza;

import java.util.List;

import it.unibas.tav.iotsentinel.DAOException;

public interface IDAOGenerico<T> {

    T findById(Long id) throws DAOException;

    List<T> findAll() throws DAOException;

    T makePersistent(T entity) throws DAOException;

    void makeTransient(T entity) throws DAOException;

}
