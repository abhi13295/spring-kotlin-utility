package com.outleap.demo.utils.file

import com.outleap.demo.utils.exceptions.getTrace
import com.outleap.demo.utils.logger
import org.springframework.http.HttpHeaders
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

object FileUtils {
    private val log = logger()

    fun getFileFromByteArray(fileName: String, byteArray: ByteArray): File {
        val file = createTempFile(fileName, true)
        val outputStream = FileOutputStream(file)
        outputStream.write(byteArray)
        outputStream.close()
        return file
    }

    fun createTempFile(requiredFileName: String, generateUniqueName: Boolean=false): File {
        var fileName = requiredFileName.replace(",","_")
        fileName = if (generateUniqueName) generateFileName(fileName) else fileName
        return File(getTempFileName(fileName))
    }

    fun generateFileName(multiPart: MultipartFile): String {
        return Date().time.toString() + "-" + multiPart.originalFilename?.replace(" ", "_")?.replace(",","_")
    }

    fun generateFileName(fileName: String): String {
        return Date().time.toString() + "-" + fileName.replace(" ", "_").replace(",","_")
    }

    fun generateFileName(file: File): String {
        return Date().time.toString() + "-" + file.name.replace(" ", "_").replace(",","_")
    }

    fun generateFileName(multiPart: MultipartFile,  customName: String): String {
        return Date().time.toString() + "-" + customName+"."+ multiPart.originalFilename
    }

    fun getTempFileName(fileName: String): String {
        return System.getProperty("java.io.tmpdir") + "/" + fileName
    }

    fun deleteSilently(file: File?) {
        try {
            if (file == null || !file.exists()) {
                return
            }
            file.delete()
        } catch (ex:Exception) {
            log.error("error in deleteSilently "+ex.getTrace())
        }
    }

    fun getFileHeaders(filename: String): HttpHeaders {
        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$filename")
        header.add("Cache-Control", "no-cache, no-store, must-revalidate")
        header.add("Pragma", "no-cache")
        header.add("Expires", "0")
        return header
    }

    fun convertMultiPartFileToFile(file: MultipartFile): File {
        val newFilePath = Paths.get(System.getProperty("java.io.tmpdir"), file.originalFilename)
        Files.copy(file.inputStream, newFilePath,
            StandardCopyOption.REPLACE_EXISTING)
        return newFilePath.toFile()
    }

    fun createDirectory(directory: String){
        val dir = getTempFileName(directory)
        File(dir).mkdir()
    }

    @Throws(IOException::class)
    fun convertMultiPartToFile(file: MultipartFile): File {
        val newFilePath = Paths.get(System.getProperty("java.io.tmpdir"), file.originalFilename)
        Files.copy(file.inputStream, newFilePath,
            StandardCopyOption.REPLACE_EXISTING)
        return newFilePath.toFile()
    }



}