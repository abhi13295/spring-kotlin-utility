package com.outleap.demo.utils.exceptions

class HandlerException(type: String): Exception("Handler for $type does not exist")