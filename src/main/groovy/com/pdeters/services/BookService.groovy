package com.pdeters.services

import com.pdeters.domain.Book
import com.pdeters.domain.repos.BookRepository
import com.pdeters.exceptions.ResourceNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BookService {

    @Autowired
    BookRepository repository

    List<Book> findAll() {
        return repository.findAll() as List
    }

    Book findById(Long id) {
        Optional optional = repository.findById(id)
        if (optional.present) {
            return optional.get()
        }
        throw new ResourceNotFoundException()
    }
}
