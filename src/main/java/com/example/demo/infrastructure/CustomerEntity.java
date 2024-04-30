package com.example.demo.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;

import java.sql.Date;

@Entity(name = "customer")
@Getter
@ToString
public class CustomerEntity {
    @Id
    @Column(name = "customerId")
    private String id;
    private String firstName;
    private String lastName;
    private String company;
    private String city;
    private String country;
    private String phone1;
    private String phone2;
    private String email;
    private Date subscriptionDate;
    private String website;
}
