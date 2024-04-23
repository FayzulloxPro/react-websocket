package com.involveininnovation.chat.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;

    @Column(unique = true)
    private String email;
    @Column
    @JsonIgnore
    private String password;
    @Column
    private double totalPurchasesAmount;
    @JsonIgnore
    @Column(name = "is_admin")
    private boolean isAdmin;



}

