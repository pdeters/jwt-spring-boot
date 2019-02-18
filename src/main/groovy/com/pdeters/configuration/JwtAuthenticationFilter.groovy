package com.pdeters.configuration

import com.pdeters.services.JwtUtils
import groovy.util.logging.Slf4j
import io.jsonwebtoken.Claims
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_PREFIX = 'Bearer '
    public static final String AUTHORIZATION = 'Authorization'

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getAuthorizationToken(request)

        authenticate(request, token)

        filterChain.doFilter(request, response)
    }

    private static String getAuthorizationToken(HttpServletRequest request) {

        String authHeader = request.getHeader(AUTHORIZATION)
        return (authHeader?.startsWith(TOKEN_PREFIX)) ? authHeader.replace(TOKEN_PREFIX,'') : null
    }

    private static void authenticate(HttpServletRequest request, String token) {

        Optional optional = JwtUtils.getClaimsFromToken(token)

        if (optional.present) {
            Claims claims = optional.get()

            List<SimpleGrantedAuthority> roles = JwtUtils.getRolesFrom(claims).collect {
                new SimpleGrantedAuthority(it)
            }

            String principal = claims.getSubject()

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, roles)

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))

            SecurityContextHolder.getContext().setAuthentication(authentication)
        }
    }
}
