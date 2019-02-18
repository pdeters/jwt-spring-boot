package com.pdeters.web

import com.pdeters.domain.Book
import com.pdeters.services.BookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = '/books', produces = MediaType.APPLICATION_JSON_VALUE)
class BookController {

    @Autowired
    BookService bookService

    @Secured('ROLE_ADMIN')
    @RequestMapping
    List<Book> findAll() {
        return bookService.findAll()
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    @RequestMapping(value = '/{id}')
    Book findById(@PathVariable('id') Long id) {
        bookService.findById(id)
    }
}
