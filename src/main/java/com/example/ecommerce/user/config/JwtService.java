package com.example.ecommerce.user.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;


@Service
public class JwtService {

    @Value("${jwt.secreteKey}")
private String secreteKey;

    @Value("${jwt.expiration}")
    private Long expiration;



    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }



    public String generateToken(UserDetails userDetails){

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .expiration(new Date(System.currentTimeMillis() + expiration) )
                .issuedAt(new Date())
                .signWith(getSignInKey())
                .compact();

    }




    public boolean isTokenValid(String token){
        try{

          return   !extractExpirationDate(token).before(new Date());

        }
        catch(Exception e){
            return false;
        }

    }


    private Date extractExpirationDate(String token){
        return extractAllClaims(token).getExpiration();
    }




    private Claims extractAllClaims(String token){

        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build().
                parseSignedClaims(token)
                .getPayload()
                ;
    }


    private SecretKey getSignInKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secreteKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
