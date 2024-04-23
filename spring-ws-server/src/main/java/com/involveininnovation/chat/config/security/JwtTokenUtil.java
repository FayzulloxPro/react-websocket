package com.involveininnovation.chat.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import com.involveininnovation.chat.dtos.auth.TokenResponse;
import com.involveininnovation.chat.enums.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static com.involveininnovation.chat.enums.TokenType.ACCESS;
import static com.involveininnovation.chat.enums.TokenType.REFRESH;

@Component
public class JwtTokenUtil {

    @Value("${jwt.access.token.expiry}")
    private long accessTokenExpiration;

    @Value("${jwt.access.token.secret.key}")
    public String ACCESS_TOKEN_SECRET_KEY;

    @Value("${jwt.refresh.token.expiry}")
    private long refreshTokenExpiration;

    @Value("${jwt.refresh.token.secret.key}")
    public String REFRESH_TOKEN_SECRET_KEY;

    public TokenResponse generateToken(@Nonnull String username) {
        TokenResponse tokenResponse = new TokenResponse();
        generateAccessToken(username, tokenResponse);
        generateRefreshToken(username, tokenResponse);
        return tokenResponse;
    }

    public TokenResponse generateRefreshToken(@Nonnull String username, @Nonnull TokenResponse tokenResponse) {
        tokenResponse.setRefreshTokenExpiry(new Date(System.currentTimeMillis() + refreshTokenExpiration));
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setIssuer("https://online.pdp.uz")
                .setExpiration(tokenResponse.getRefreshTokenExpiry())
                .signWith(signKey(REFRESH), SignatureAlgorithm.HS256)
                .compact();
        tokenResponse.setRefreshToken(refreshToken);
        return tokenResponse;
    }

    public TokenResponse generateAccessToken(String username, @Nonnull TokenResponse tokenResponse) {
        tokenResponse.setAccessTokenExpiry(new Date(System.currentTimeMillis() + accessTokenExpiration));
        String accessToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setIssuer("https://online.pdp.uz")
                .setExpiration(tokenResponse.getAccessTokenExpiry())
                .signWith(signKey(ACCESS), SignatureAlgorithm.HS256)
                .compact();
        tokenResponse.setAccessToken(accessToken);
        return tokenResponse;
    }

    private Key signKey(TokenType tokenType) {
        byte[] bytes = Decoders.BASE64.decode((tokenType.equals(ACCESS)) ? ACCESS_TOKEN_SECRET_KEY : REFRESH_TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }


    public boolean isValid(String token, TokenType tokenType) {
        try {
            Claims claims = getClaims(token, tokenType);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Claims getClaims(String token, TokenType tokenType) {
        return Jwts.parserBuilder()
                .setSigningKey(signKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsername(String token, TokenType tokenType) {
        Claims claims = getClaims(token, tokenType);
        return claims.getSubject();
    }

    public Date getExpiry(String refreshToken, TokenType tokenType) {
        Claims claims = getClaims(refreshToken, tokenType);
        return claims.getExpiration();
    }
}
