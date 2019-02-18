package com.pdeters.services

import com.pdeters.web.JwtTokenContext
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

    static Optional<Claims> getClaimsFromToken(String token) {

        if (!token) {
            return Optional.empty()
        }

        try {
            Claims claims = Jwts.parser().setSigningKey(AuthService.getSecret()).parseClaimsJws(token).getBody()

            return Optional.of(claims)

        } catch (IllegalArgumentException | ExpiredJwtException | SignatureException | MalformedJwtException e) {
            log.debug("Unable to parse auth token, msg: ${e.message}")
        }

        return Optional.empty()
    }

    static List<String> getRolesFrom(String token) {
        return getRolesFrom(getClaimsFromToken(token))
    }

    static List<String> getRolesFrom(Optional<Claims> optional) {
        return optional.present ? getRolesFrom(optional.get()) : []
    }

    static List<String> getRolesFrom(Claims claims) {
        JwtTokenContext context = claims.get('context') as JwtTokenContext
        return context.roles
    }
}