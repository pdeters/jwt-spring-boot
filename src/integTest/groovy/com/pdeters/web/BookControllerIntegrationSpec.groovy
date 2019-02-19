package com.pdeters.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerIntegrationSpec extends Specification {

    @Autowired
    WebApplicationContext wac

    MockMvc api

    def setup() {
        api = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build()
    }

    def 'can fetch all books only as an admin'() {
        expect:
        api.perform(get('/books'))
                .andExpect(status().isUnauthorized())

        when:
        String authHeader = generateJwtAuthorizationHeaderFor('administrator')

        then:
        api.perform(get('/books').header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())

        when:
        authHeader = generateJwtAuthorizationHeaderFor('user')

        then:
        api.perform(get('/books').header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isForbidden())
    }

    def 'can fetch a book as a user or an admin'() {
        expect:
        api.perform(get('/books/2'))
                .andExpect(status().isUnauthorized())

        when:
        String authHeader = generateJwtAuthorizationHeaderFor('administrator')

        then:
        api.perform(get('/books/2').header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())

        when:
        authHeader = generateJwtAuthorizationHeaderFor('user')

        then:
        api.perform(get('/books/2').header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
    }

    def 'can not fetch a book with a bad id'() {
        expect:
        api.perform(get('/books/99999'))
                .andExpect(status().isUnauthorized())

        when:
        String authHeader = generateJwtAuthorizationHeaderFor('administrator')

        then:
        api.perform(get('/books/99999').header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isNotFound())

        when:
        authHeader = generateJwtAuthorizationHeaderFor('user')

        then:
        api.perform(get('/books/99999').header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isNotFound())
    }

    def 'get proper response for an expired token'() {
        expect:
        api.perform(get('/books').header(HttpHeaders.AUTHORIZATION, EXPIRED_AUTH_HEADER))
                .andExpect(status().isUnauthorized())
    }

    def 'get proper response for a token from a different issuer'() {
        expect:
        api.perform(get('/books').header(HttpHeaders.AUTHORIZATION, WRONG_ISSUER_AUTH_HEADER))
                .andExpect(status().isUnauthorized())
    }

    String generateJwtAuthorizationHeaderFor(String username) {
        MvcResult result = api.perform(post('/auth/token').contentType(MediaType.APPLICATION_JSON)
                .content("""{ "username": "$username", "password": "password" }"""))
                .andExpect(status().isCreated())
                .andReturn()

        String response = result.getResponse().getContentAsString()
        AuthToken auth = new ObjectMapper().readValue(response, AuthToken)

        return "Bearer ${auth.token}"
    }

    static String EXPIRED_AUTH_HEADER = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhcHAtdG9rZW4taXNzdWVyIiwic3ViIjoiYWRta\
W5pc3RyYXRvciIsImlhdCI6MTU1MDU5MjMyMiwiY29udGV4dCI6eyJyb2xlcyI6WyJST0xFX0FETUlOIl0sImRpc3BsYXlOYW1lIjoiU2FsbHkgQWRtaW4\
ifSwiZXhwIjoxNTUwNTkyMDIyfQ.ea7qs39R0xtdZPh-IbWKjrQ6BdHQ0NCKjoclILyGFHM"

    static String WRONG_ISSUER_AUTH_HEADER = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ3cm9uZy1pc3N1ZXIiLCJzdWIiOiJ1c2Vy\
IiwiaWF0IjoxNTUwNTkzMDQyLCJjb250ZXh0Ijp7InJvbGVzIjpbIlJPTEVfVVNFUiJdLCJkaXNwbGF5TmFtZSI6IkpvZSBVc2VyIn0sImV4cCI6MTU1MD\
U5MzM0Mn0.Bqqoyaixy6ubWvNQwXUvABx8f85jLIGAAAl97iFDOQs"
}
