package com.example.demo.datafetcher;

import com.example.demo.codegen.types.Customer;
import com.example.demo.infrastructure.CustomerEntity;
import com.example.demo.infrastructure.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@DgsComponent
public class CustomerDataFetcher {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @DgsQuery
    public Customer getCustomer(@InputArgument String id) {
        CustomerEntity customer;

        try {
            customer = customerRepository
                    .findById(id)
                    .orElseThrow();
        } catch (Exception e) {
            log.error("Exception getting customer: " + e.getMessage());

            throw new DgsEntityNotFoundException("Customer not found for id " + id);
        }

        publishCustomerRetrievedMessage(customer.getId());

        return Customer
                .newBuilder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .company(customer.getCompany())
                .city(customer.getCity())
                .country(customer.getCountry())
                .phone1(customer.getPhone1())
                .phone2(customer.getPhone2())
                .email(customer.getEmail())
                .subscriptionDate(customer.getSubscriptionDate().toString())
                .website(customer.getWebsite())
                .build();
    }

    public void publishCustomerRetrievedMessage(String id) {
        kafkaTemplate.send(
            "customer",
            "Customer with id " + id + " retrieved"
        );
    }
}
