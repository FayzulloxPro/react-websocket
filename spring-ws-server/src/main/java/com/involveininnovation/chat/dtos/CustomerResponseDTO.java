package com.involveininnovation.chat.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerResponseDTO {

    private String name;
    private String email;
    private double totalPurchasesAmount;
}
