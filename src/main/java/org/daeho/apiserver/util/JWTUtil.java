package org.daeho.apiserver.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Map;

@Log4j2
public class JWTUtil {
    private static String key = "32189471032091743912730912733729332322";
    public static String generateToken(Map<String,Object> valueMap, int min){
        SecretKey key = null;
        try{
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ","JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();
        return jwtStr;
    }

    public static Map<String, Object> validateToken(String token){
        Map<String, Object> claim = null;
        try {
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        }catch (MalformedJwtException malformedJwtException){
            throw new CustomJWTException("MalFormed");
        }catch (ExpiredJwtException expiredJwtException){
            throw new CustomJWTException("Expired");
        }catch (InvalidClaimException invalidClaimException){
            throw new CustomJWTException("Invalid");
        }catch (JwtException jwtException){
            throw new CustomJWTException("JWTError");
        }catch (Exception e){
            throw new CustomJWTException("Error");
        }
        return claim;
    }
}
