package com.pdeters.domain.repos

import com.pdeters.domain.Book
import org.springframework.data.repository.CrudRepository

interface BookRepository extends CrudRepository<Book, Long> { }
