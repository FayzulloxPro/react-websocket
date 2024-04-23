package com.involveininnovation.chat.repository;

import com.involveininnovation.chat.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("select c from Customer c where upper(c.email)=upper(?1)")
    Optional<Customer> findByEmail(String username);
}