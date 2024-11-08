package com.example.auth_spring_security.Service;

import com.example.auth_spring_security.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

   public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
   }

   public Date extractExpiration(String token){
       return extractClaims(token,Claims::getExpiration);
   }

   public <T> T extractClaims(String token, Function<Claims,T> claimsTFunction){
       final Claims claims=extractAllClaims(token);
       return claimsTFunction.apply(claims);
   }

    public String generateToken(User user){
    return generateToken(user,new HashMap<>());
   }

    public String generateToken(User user,Map<String,Object> extraClaims){
        return build(extraClaims,user,jwtExpiration);
    }

    public String generateRefreshingToken(User user){
       return build(new HashMap<>(),user,refreshExpiration);
    }

   public String build(Map<String,Object> extraClaims, User user, long expiration){
    return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(user.getName())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis()+expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
}

    public boolean isTokenValid(String token, User userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getName())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
