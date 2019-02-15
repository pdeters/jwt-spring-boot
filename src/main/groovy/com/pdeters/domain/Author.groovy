package com.pdeters.domain

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.*

@Entity
@Table(name = 'AUTHORS')
class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @Column
    String firstName

    @Column
    String lastName

    @JsonIgnore
    @OneToMany(mappedBy = 'author')
    List<Book> book
}
