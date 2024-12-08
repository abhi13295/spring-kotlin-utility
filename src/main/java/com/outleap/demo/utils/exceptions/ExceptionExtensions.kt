package com.outleap.demo.utils.exceptions

import org.apache.commons.lang3.exception.ExceptionUtils

fun Exception.getTrace(): String{
    return ExceptionUtils.getStackTrace(this)
}