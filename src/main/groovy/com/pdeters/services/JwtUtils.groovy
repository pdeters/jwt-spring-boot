package com.pdeters.services

import groovy.util.logging.Slf4j
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException

import java.security.SignatureException

@Slf4j
class JwtUtils {

    static boolean isValidToken(String token) {
        return getClaimsFromToken(token).present
    }

    static List getRolesFromToken(String token) {

        Optional optional = getClaimsFromToken(token)

        if (optional.present) {
            Claims claims = optional.get()

            Map context = claims.get('context') as Map

            return context.get('roles') as List
        }

        return []
    }

    private static Optional<Claims> getClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(AuthService.getSecret()).parseClaimsJws(token).getBody()

            return Optional.of(claims)

        } catch (IllegalArgumentException | ExpiredJwtException | SignatureException | MalformedJwtException e) {
            log.debug("Unable to parse auth token, msg: ${e.message}")
        }

        return Optional.empty()
    }
}