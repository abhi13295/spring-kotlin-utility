package com.outleap.demo.utils.exceptions

open class CustomException(list: List<String>) : Exception(list.joinToString()) {
    constructor(message: String) : this(listOf(message))
}