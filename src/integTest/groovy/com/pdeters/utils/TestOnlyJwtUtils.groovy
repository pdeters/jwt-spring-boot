package com.pdeters.utils

import com.pdeters.web.JwtTokenContext
import io.jsonwebtoken.Claims

class TestOnlyJwtUtils extends JwtUtils {

    static List<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token).getBody()

        JwtTokenContext context = claims?.get('context') as JwtTokenContext

        return context.roles
    }
}
