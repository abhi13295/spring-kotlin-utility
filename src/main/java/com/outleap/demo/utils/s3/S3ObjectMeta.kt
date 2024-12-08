package com.outleap.demo.utils.s3

data class S3ObjectMeta(
    var contentType: String? = null,
    var data: ByteArray,
    var fileName: String = "File"
)