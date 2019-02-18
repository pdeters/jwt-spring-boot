package com.pdeters.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.pdeters.services.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationSpec extends Specification{

    @Autowired
    WebApplicationContext wac

    MockMvc api

    def setup() {
        api = MockMvcBuilders.webAppContextSetup(wac).build()
    }

    def 'can generate a token for an admin'() {
        expect:
        MvcResult result = api.perform(post('/auth/token').contentType(MediaType.APPLICATION_JSON)
                .content('''{ "username": "administrator", "password": "password" }'''))
                .andExpect(status().isCreated())
                .andReturn()

        when:
        String response = result.getResponse().getContentAsString()
        AuthToken auth = new ObjectMapper().readValue(response, AuthToken)

        then:
        JwtUtils.isValidToken(auth.token)

        and:
        List roles = JwtUtils.getRolesFrom(auth.token)

        then:
        roles.size() == 1
        roles.contains('ROLE_ADMIN')
    }

    def 'can generate a token for a user'() {
        expect:
        MvcResult result = api.perform(post('/auth/token').contentType(MediaType.APPLICATION_JSON)
                .content('''{ "username": "user", "password": "password" }'''))
                .andExpect(status().isCreated())
                .andReturn()

        when:
        String response = result.getResponse().getContentAsString()
        AuthToken auth = new ObjectMapper().readValue(response, AuthToken)

        then:
        JwtUtils.isValidToken(auth.token)

        and:
        List roles = JwtUtils.getRolesFrom(auth.token)

        then:
        roles.size() == 1
        roles.contains('ROLE_USER')
    }

    def 'get proper response for bad credentials'() {
        expect:
        api.perform(post('/auth/token').contentType(MediaType.APPLICATION_JSON)
                .content('''{ "username": "user", "password": "wrong" }'''))
                .andExpect(status().isUnauthorized())
                .andReturn()
    }
}
