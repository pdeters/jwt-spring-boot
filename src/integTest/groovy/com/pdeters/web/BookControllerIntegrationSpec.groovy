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

    String generateJwtAuthorizationHeaderFor(String username) {
        MvcResult result = api.perform(post('/auth/token').contentType(MediaType.APPLICATION_JSON)
                .content("""{ "username": "$username", "password": "password" }"""))
                .andExpect(status().isCreated())
                .andReturn()

        String response = result.getResponse().getContentAsString()
        AuthToken auth = new ObjectMapper().readValue(response, AuthToken)

        return "Bearer ${auth.token}"
    }
}
