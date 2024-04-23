package com.involveininnovation.chat.dtos;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateOrderRequestDTO {

    @NotNull
    @ElementCollection
    @Positive(message = "Each element must be positive")
    private List<Long> books;
}
