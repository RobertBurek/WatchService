package com.example.microservices.repsitory;

import com.example.microservices.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryApi extends CrudRepository<Transaction, Long> {

}
