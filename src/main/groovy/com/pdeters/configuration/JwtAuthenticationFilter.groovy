package com.pdeters.configuration

import com.pdeters.utils.JwtUtils
import groovy.util.logging.Slf4j
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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

        if (JwtUtils.isValidToken(token)) {

            UsernamePasswordAuthenticationToken authentication = JwtUtils.getAuthentication(token)

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))

            SecurityContextHolder.getContext().setAuthentication(authentication)
        }

        filterChain.doFilter(request, response)
    }

    private static String getAuthorizationToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION)
        return authHeader?.startsWith(TOKEN_PREFIX) ? authHeader.replace(TOKEN_PREFIX,'') : null
    }
}
