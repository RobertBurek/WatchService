package com.example.microservices.repsitory;

import com.example.microservices.model.Transaction;

import java.sql.SQLException;

public interface RepoApi {

    void createTables(String nameTable) throws SQLException;

    void saveAllToTable(String nameTable, Iterable<Transaction> listT);

    boolean isExistTable(String nameTable);
}
