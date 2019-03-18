package com.bcb.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * Created by kx on 2018/1/2.
 */
public class TokenUtil {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String SECRET = "bcb2018";
    public static String createJwtToken(String loginName){
        String issuer = "www.bcb.com";
        long millis = System.currentTimeMillis();
        return createJwtToken(loginName, issuer, millis);
    }

    public static String createJwtToken(String loginName, String issuer, long millis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder().setId(loginName)
                .setIssuedAt(now)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);
        if (millis >= 0) {
            long expMillis = nowMillis + millis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    public static Claims parseJWT(String jwt) {
        Claims claims = Jwts.parser()
            .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
            .parseClaimsJws(jwt).getBody();
        return claims;
    }
}
