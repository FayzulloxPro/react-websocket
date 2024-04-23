package com.involveininnovation.chat.controller;

import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.involveininnovation.chat.dtos.CustomerResponseDTO;
import com.involveininnovation.chat.dtos.RefreshTokenRequest;
import com.involveininnovation.chat.dtos.auth.CustomerCreateDTO;
import com.involveininnovation.chat.dtos.auth.TokenRequest;
import com.involveininnovation.chat.dtos.auth.TokenResponse;
import com.involveininnovation.chat.entity.Customer;
import com.involveininnovation.chat.exception.DuplicateUsernameException;
import com.involveininnovation.chat.mapper.CustomerMapper;
import com.involveininnovation.chat.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;
    private final CustomerMapper customerMapper;

    @PostMapping("/access/token")
    public ResponseEntity<TokenResponse> generateToken(@RequestBody @Valid TokenRequest tokenRequest) {
        return ResponseEntity.ok(authService.generateToken(tokenRequest));
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDTO> register(@Nonnull @Valid @RequestBody CustomerCreateDTO customerCreateDTO) throws DuplicateUsernameException {
        Customer customer = authService.create(customerCreateDTO);
        return ResponseEntity.ok(customerMapper.toDTO(customer));
    }
}
