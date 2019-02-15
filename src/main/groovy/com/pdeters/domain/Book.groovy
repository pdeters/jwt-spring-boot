package com.pdeters.domain

import javax.persistence.*

@Entity
@Table(name = 'BOOKS')
class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @Column
    String title

    @ManyToOne
    @JoinColumn(name = 'author_id')
    Author author
}


