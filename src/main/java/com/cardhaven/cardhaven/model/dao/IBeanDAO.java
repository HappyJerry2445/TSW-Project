package com.cardhaven.cardhaven.model.dao;

import java.sql.SQLException;
import java.util.Collection;

public interface IBeanDAO<T> {
    void doSave(T t) throws SQLException;

    boolean doDelete(T t) throws SQLException;

    T doRetrieveByKey(int code) throws SQLException;

    Collection<T> doRetrieveAll(String order) throws SQLException;
}
