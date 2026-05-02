package it.unibas.tav.iotsentinel.persistenza.regola;

import java.util.List;

import it.unibas.tav.iotsentinel.modello.regola.IRegola;

public interface DAORegole {

    IRegola makePersistent(IRegola regola);

    void makeTransient(IRegola regola);

    List<IRegola> findAll();
}
