package com.outleap.demo.dto

class CustomerResponseDto {
    var name: String? = null
    var email: String? = null
    var contactNumber: String? = null

    internal constructor(name: String?, email: String?, contactNumber: String?) {
        this.name = name
        this.email = email
        this.contactNumber = contactNumber
    }

    internal constructor()
}
