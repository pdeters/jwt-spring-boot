package com.pdeters.exceptions

import groovy.transform.InheritConstructors
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@InheritConstructors
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException extends RuntimeException { }
