package com.pdeters.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerIntegrationSpec extends Specification {

    @Autowired
    WebApplicationContext wac

    MockMvc api

    def setup() {
        api = MockMvcBuilders.webAppContextSetup(wac).build()
    }

    def 'can fetch books'() {
        expect:
        api.perform(get('/books'))
            .andExpect(status().isOk())
    }

    def 'can fetch a book1'() {
        expect:
        api.perform(get('/books/2'))
                .andExpect(status().isOk())
    }

    def 'can not fetch a book with a bad id'() {
        expect:
        api.perform(get('/books/99999'))
                .andExpect(status().isNotFound())
    }
}
