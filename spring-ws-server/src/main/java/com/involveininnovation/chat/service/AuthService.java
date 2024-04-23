package com.involveininnovation.chat.service;

import com.involveininnovation.chat.dtos.RefreshTokenRequest;
import com.involveininnovation.chat.dtos.auth.CustomerCreateDTO;
import com.involveininnovation.chat.dtos.auth.TokenRequest;
import com.involveininnovation.chat.dtos.auth.TokenResponse;
import com.involveininnovation.chat.entity.Customer;
import com.involveininnovation.chat.exception.DuplicateUsernameException;
import org.springframework.lang.NonNull;

public interface AuthService {
    TokenResponse generateToken(@NonNull TokenRequest tokenRequest);

    TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    Customer create(CustomerCreateDTO customerCreateDTO) throws DuplicateUsernameException;

    Customer findById(Long id);
}
