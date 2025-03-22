package com.luis.blogapp.JwtUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secret;

    public DecodedJWT decodedToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .build();
        return verifier.verify(token);
    }

    public String extractUsername(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        return decodedJWT.getSubject();
    }
}
