package com.outleap.demo.utils.exceptions

import com.outleap.demo.dto.ResponseWrapper
import com.outleap.demo.utils.ErrorMessages


object ExceptionUtils {
    fun <T> handleException(ex: Exception): ResponseWrapper<T> {
        return when(ex){
            is CustomException, is UnAuthorisedException, is HandlerException -> {
                ResponseWrapper(success = false, message = ex.message, data = null)
            }
            else -> {
                ResponseWrapper(success = false, message = ErrorMessages.GENERIC_ERROR_MESSAGE, data = null)
            }
        }
    }
}