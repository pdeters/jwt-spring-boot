package com.pdeters.utils

import com.pdeters.services.AuthService
import com.pdeters.web.JwtTokenContext
import groovy.util.logging.Slf4j
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

import java.security.SignatureException

@Slf4j
class JwtUtils {

    static boolean isValidToken(String token) {
        try {
            parseToken(token)
            return true
        } catch (IllegalArgumentException | ExpiredJwtException | SignatureException | MalformedJwtException e) {
            log.warn("Unable to parse auth token, msg: ${e.message}")
        }
        return false
    }

    static UsernamePasswordAuthenticationToken getAuthentication(String token) {

        Claims claims = parseToken(token).getBody()

        JwtTokenContext context = claims?.get('context') as JwtTokenContext

        List<SimpleGrantedAuthority> roles = context.roles.collect { new SimpleGrantedAuthority(it) }

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, roles)
    }

    protected static Jws parseToken(String token) {
        return Jwts.parser().setSigningKey(AuthService.getSecret()).parseClaimsJws(token)
    }
}