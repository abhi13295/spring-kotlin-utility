package com.outleap.demo.dto

/**
 * @author abhijeet
 * Created: 29/06/20
 */
data class ResponseWrapper<T> (
        val success: Boolean,
        val message: String? = null,
        val data: T?
)
