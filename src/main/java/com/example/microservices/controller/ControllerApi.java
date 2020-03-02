package com.example.microservices.controller;

import com.example.microservices.model.Transaction;
import com.example.microservices.repsitory.RepositoryApi;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.microservices.MicroservicesApplication.wierszePliku;


@Log4j2
@RestController
@RequestMapping(value = "dane")
public class ControllerApi {

    @Autowired
    RepositoryApi repositoryApi;


    @GetMapping(value = "/{firstName}/{lastName}")
    public String hello(@PathVariable("firstName") String firstName,
                        @PathVariable("lastName") String lastName) {
        String odpFormat = String.format("{\"message\":\"Hello %s %s\"}",
                firstName, lastName);
        log.info("Odpowiedź na GET: " + odpFormat);
        return odpFormat;
    }

    @GetMapping(value = "")
    public Iterable<Transaction> helloList() {
        return repositoryApi.findAll();
    }

    @GetMapping(value = "/zpliku")
    public List<String> helloListPlik() {
        return wierszePliku;
    }

}
