package com.pdeters.web

import com.pdeters.services.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = '/auth/token', produces = MediaType.APPLICATION_JSON_VALUE)
class AuthController {

    @Autowired
    AuthService authService

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<AuthToken> generateToken(@RequestBody Credentials credentials) {

        boolean authenticated = authService.authenticate(credentials.username, credentials.password)

        if (authenticated) {

            AuthToken authToken = authService.generateToken(credentials.username)

            return new ResponseEntity(authToken, HttpStatus.CREATED)
        }

        throw new BadCredentialsException()
    }
}
