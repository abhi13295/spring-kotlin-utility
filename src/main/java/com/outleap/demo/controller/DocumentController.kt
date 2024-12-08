package com.outleap.demo.controller

import com.outleap.demo.dto.ResponseWrapper
import com.outleap.demo.facade.DocumentFacade
import com.outleap.demo.utils.logger
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("v1/document")
class DocumentController (
    private val documentFacade: DocumentFacade
) {

    private val log = logger()

    @PostMapping("")
    private fun uploadDocument(
        @RequestParam("fileLabel", required = true) fileLabel: String?,
        @RequestPart(value = "file", required = true) file: MultipartFile
    ): ResponseWrapper<String> {
        log.info("inside uploadDocument")
        val response = documentFacade.uploadDocument(file, fileLabel)
        return ResponseWrapper(success = true, message = "success", data = response)
    }
}