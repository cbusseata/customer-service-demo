package com.example.demo.infrastructure;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
    @Override
    @Cacheable(value = "customer", key = "#id")
    Optional<CustomerEntity> findById(String id);

    @CacheEvict(value = "customer", allEntries = true)
    void deleteAll();

    @Override
    @CacheEvict(value = "customer", key = "#p0.getId()")
    <S extends CustomerEntity> S save(S entity);
}
