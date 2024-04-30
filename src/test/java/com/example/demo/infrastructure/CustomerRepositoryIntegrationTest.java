package com.example.demo.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import static org.mockito.Mockito.*;

@ContextConfiguration
@ExtendWith(SpringExtension.class)
public class CustomerRepositoryIntegrationTest {
    private static final String CUSTOMER_ID = "DD37Cf93aecA6Dc";

    private CustomerRepository mockCustomerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CacheManager cacheManager;

    @EnableCaching
    @Configuration
    public static class CachingTestConfig {
        @Bean
        public CustomerRepository customerRepository() {
            return mock(CustomerRepository.class);
        }
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("customer");
        }
    }

    @BeforeEach
    void setUp() {
        // customerRepository is a proxy around our mock. So, in order to use Mockito
        // validations, we retrieve the actual mock via AopTestUtils.getTargetObject
        mockCustomerRepository = AopTestUtils.getTargetObject(customerRepository);

        reset(mockCustomerRepository);
    }

    @Test
    void aCustomerIsReturnedFromCache() {
        CustomerEntity customer = new CustomerEntity();

        when(mockCustomerRepository.findById(CUSTOMER_ID))
            .thenReturn(Optional.of(customer));

        CustomerEntity cacheMissCustomer = customerRepository.findById(CUSTOMER_ID).get();
        verify(mockCustomerRepository).findById(CUSTOMER_ID);

        CustomerEntity cacheHitCustomer = customerRepository.findById(CUSTOMER_ID).get();
        // Shouldn't get called again with a cache hit
        verifyNoMoreInteractions(mockCustomerRepository);

        Assertions.assertEquals(cacheMissCustomer, customer);
        Assertions.assertEquals(cacheHitCustomer, customer);

        verify(mockCustomerRepository, times(1)).findById(CUSTOMER_ID);
        Assertions.assertEquals(
            cacheManager
                .getCache("customer")
                .get(CUSTOMER_ID)
                .get(),
            customer
        );
    }
}
