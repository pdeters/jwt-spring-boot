package com.pdeters.services

import com.pdeters.web.AuthToken
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service

import javax.xml.bind.DatatypeConverter

@Service
class AuthService {

    private static final String SECRET = 'strong-secret'

    private final String ONLY_PASSWORD = 'password'

    static byte[] getSecret() {
        return DatatypeConverter.parseBase64Binary(SECRET)
    }

    boolean authenticate(String user, String password) {
        return user && (password == ONLY_PASSWORD)
    }

    static AuthToken generateToken(String username) {
        Date now = new Date()

        Map userContext = getUserContextFor(username)

        JwtBuilder builder = Jwts.builder()
                .setIssuer('app-token-issuer')
                .setSubject(username)
                .setIssuedAt(now)
                .addClaims(userContext)
                .setExpiration(addMinutes(now, 5))

        String token = builder.signWith(SignatureAlgorithm.HS256, getSecret()).compact()

        return new AuthToken(token: token)
    }

    private static Map getUserContextFor(String username) {
        if (username == 'administrator') {
            return [context: [roles: ['ROLE_ADMIN'], displayName: 'Sally Admin']]
        } else {
            return [context: [roles: ['ROLE_USER'], displayName: 'Joe User']]
        }
    }

    private static Date addMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.MINUTE, minutes)
        return cal.getTime()
    }
}
