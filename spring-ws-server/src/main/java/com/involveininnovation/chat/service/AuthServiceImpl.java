package com.involveininnovation.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.involveininnovation.chat.config.security.JwtTokenUtil;
import com.involveininnovation.chat.dtos.RefreshTokenRequest;
import com.involveininnovation.chat.dtos.auth.CustomerCreateDTO;
import com.involveininnovation.chat.dtos.auth.TokenRequest;
import com.involveininnovation.chat.dtos.auth.TokenResponse;
import com.involveininnovation.chat.entity.Customer;
import com.involveininnovation.chat.enums.TokenType;
import com.involveininnovation.chat.exception.DuplicateUsernameException;
import com.involveininnovation.chat.exception.ItemNotFoundException;
import com.involveininnovation.chat.mapper.CustomerMapper;
import com.involveininnovation.chat.repository.CustomerRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final CustomerRepository customerRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse generateToken(@NonNull TokenRequest tokenRequest) {
        String email = tokenRequest.email();
        String password = tokenRequest.password();

        log.debug("Generating token for user: {}", email);

        Customer byEmail = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with %s email".formatted(email)));


        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException exception) {
            throw new BadCredentialsException("Bad credentials");
        }
        return jwtTokenUtil.generateToken(email);
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();

        log.debug("Refreshing token");

        if (!jwtTokenUtil.isValid(refreshToken, TokenType.REFRESH)) {
            log.error("Invalid refresh token");
            throw new CredentialsExpiredException("Token is invalid");
        }

        String email = jwtTokenUtil.getUsername(refreshToken, TokenType.REFRESH);
        Customer byUsername = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with %s email".formatted(email)));

        TokenResponse tokenResponse = TokenResponse.builder()
                .refreshToken(refreshToken)
                .refreshTokenExpiry(jwtTokenUtil.getExpiry(refreshToken, TokenType.REFRESH))
                .build();

        return jwtTokenUtil.generateAccessToken(email, tokenResponse);
    }

    @Override
    public Customer create(CustomerCreateDTO customerCreateDTO) throws DuplicateUsernameException {
        Customer customer = customerMapper.toEntity(customerCreateDTO);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        try {
            return customerRepository.save(customer);
        } catch (Exception e) {
            throw new DuplicateUsernameException("User already exists with this email %s".formatted(customerCreateDTO.email()));
        }
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("User not found"));
    }
}
