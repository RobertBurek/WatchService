package com.example.microservices.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String dateT;
    String timeT;
    String courseT;
    String volumeT;

    public Transaction() {
    }

    public Transaction(String dateT, String timeT, String courseT, String volumeT) {
        this.dateT = dateT;
        this.timeT = timeT;
        this.courseT = courseT;
        this.volumeT = volumeT;
    }

}
