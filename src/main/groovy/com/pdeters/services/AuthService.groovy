package com.pdeters.services

import com.pdeters.web.AuthToken
import com.pdeters.web.JwtTokenContext
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
        // If any non empty user knows the password, they're good
        return user && (password == ONLY_PASSWORD)
    }

    static AuthToken generateToken(String username) {
        Date now = new Date()

        JwtTokenContext userContext = getUserContextFor(username)

        JwtBuilder builder = Jwts.builder()
                .setIssuer('wrong-issuer')
                .setSubject(username)
                .setIssuedAt(now)
                .addClaims(userContext as Map)
                .setExpiration(addMinutes(now, Integer.MAX_VALUE))

        String token = builder.signWith(SignatureAlgorithm.HS256, getSecret()).compact()

        println token

        return new AuthToken(token: token)
    }

    private static JwtTokenContext getUserContextFor(String username) {
        if (username == 'administrator') {
            return new JwtTokenContext(roles: ['ROLE_ADMIN'], displayName: 'Sally Admin')
        } else {
            return new JwtTokenContext(roles: ['ROLE_USER'], displayName: 'Joe User')
        }
    }

    private static Date addMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        //cal.add(Calendar.MINUTE, minutes)
        cal.add(Calendar.YEAR, 10)
        return cal.getTime()
    }
}
