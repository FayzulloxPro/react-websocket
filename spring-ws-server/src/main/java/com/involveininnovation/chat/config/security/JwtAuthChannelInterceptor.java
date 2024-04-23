package com.involveininnovation.chat.config.security;

import com.involveininnovation.chat.enums.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

public class JwtAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
            if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                String token = authorizationHeaders.get(0).substring(7);
                if (jwtTokenUtil.isValid(token, TokenType.ACCESS)) {
                    String username = jwtTokenUtil.getUsername(token, TokenType.ACCESS);
                    if (username != null) {
                        accessor.setUser(new UserPrincipal() {
                            @Override
                            public String getName() {
                                return username;
                            }
                        });
                        return message;
                    }
                }
            }
            throw new MessagingException("Invalid or missing JWT token");
        }
        return message;
    }
}
