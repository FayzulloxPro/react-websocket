package com.involveininnovation.chat.mapper;

import com.involveininnovation.chat.dtos.CustomerResponseDTO;
import com.involveininnovation.chat.dtos.auth.CustomerCreateDTO;
import com.involveininnovation.chat.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public Customer toEntity(CustomerCreateDTO customerCreateDTO) {
        return Customer.builder()
                .email(customerCreateDTO.email())
                .password(customerCreateDTO.password())
                .name(customerCreateDTO.name())
                .build();
    }

    public CustomerResponseDTO toDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .email(customer.getEmail())
                .name(customer.getName())
                .totalPurchasesAmount(customer.getTotalPurchasesAmount())
                .build();
    }
}
