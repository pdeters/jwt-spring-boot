package com.pdeters.web

import groovy.transform.Immutable

@Immutable
class JwtTokenContext {

    List<String> roles = []
    String displayName

    Object asType(Class clazz) {
        if (clazz == Map) {
            return [context: [roles: roles, displayName: displayName]] as Map
        }
        return super.asType(clazz)
    }
}
