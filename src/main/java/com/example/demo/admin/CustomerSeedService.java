package com.example.demo.admin;

import com.example.demo.infrastructure.CustomerEntity;
import com.example.demo.infrastructure.CustomerRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CustomerSeedService {
    @Autowired
    private CustomerRepository customerRepository;

    @Async
    public CompletableFuture<Integer> seedCustomers() {
        long start = System.currentTimeMillis();

        Integer seedCount = 0;

        try {
            seedCount = doCustomerSeeding();

            log.info(
                "Successfully seeded " + seedCount + " customers in " +
                    (System.currentTimeMillis() - start) + "ms"
            );
        } catch (SQLException sqle) {
            log.error("SQL Exception caught during customer seeding");
            return CompletableFuture.completedFuture(0);
        }

        return CompletableFuture.completedFuture(seedCount);
    }

    @Transactional(rollbackFor = { SQLException.class })
    private Integer doCustomerSeeding() throws SQLException {
        ObjectReader oReader = new CsvMapper()
                .readerFor(CustomerEntity.class)
                .with(CsvSchema.emptySchema().withHeader());
        Integer seedCount = 0;

        try {
            File file = ResourceUtils.getFile("classpath:db/seed/customers.csv");
            MappingIterator<CustomerEntity> mi = oReader.readValues(file);

            log.info("Deleting all current customers...");
            customerRepository.deleteAll();

            while (mi.hasNext()) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("Seeding customer interrupted after seeding " + seedCount + " customers");

                    throw new SQLException("Thread interrupted, rolling back transaction");
                }

                CustomerEntity nextCustomer = mi.nextValue();
                log.info("Seeding customer " + nextCustomer);
                customerRepository.save(nextCustomer);
                seedCount++;

                log.debug("Sleeping for 100ms");
                Thread.sleep(100);
            }
        } catch (IOException ioe) {
            log.error("Caught exception after seeding " + seedCount + " customers: " + ioe.getMessage());

            throw new SQLException("IOException caught, rolling back transaction");
        } catch (InterruptedException e) {
            log.info("Seeding customer interrupted after seeding " + seedCount + " customers");

            throw new SQLException("Thread interrupted, rolling back transaction");
        }

        return seedCount;
    }
}
