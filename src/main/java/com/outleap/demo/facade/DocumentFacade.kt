package com.outleap.demo.facade

import com.outleap.demo.utils.logger
import com.outleap.demo.utils.s3.AmazonClient
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class DocumentFacade (
    private val amazonClient: AmazonClient
) {

    private val log = logger()

    fun uploadDocument(file: MultipartFile,  fileLabel: String?): String {
        val url = this.amazonClient.uploadFile(file, "test", "abhinav")
        return url
    }
}