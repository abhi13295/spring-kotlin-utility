package com.outleap.demo.utils.s3

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import com.amazonaws.util.IOUtils
import com.outleap.demo.utils.CacheNames
import com.outleap.demo.utils.exceptions.getTrace
import com.outleap.demo.utils.file.FileUtils
import com.outleap.demo.utils.logger
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


@Slf4j
@Component
class AmazonClient(@param:Qualifier("IAM") private val s3client: AmazonS3) {

    val log = logger()

    @Value("\${s3.documents.bucket}")
    private val bucketName: String? = null

    @Throws(Exception::class)
    fun getFileByName(fileName: String, applicationId: String, assetType: String): S3ObjectMeta {
        log.info("entered getFileName with ")
        var obj: S3Object? = null
        return try {
            obj = s3client.getObject(bucketName, "$applicationId/$assetType/$fileName")
            val stream = obj.objectContent
            val content = IOUtils.toByteArray(stream)
            val s3ObjectMeta = S3ObjectMeta(data = content, fileName = fileName, contentType = obj.objectMetadata.contentType)
            s3ObjectMeta
        }catch (e: Exception){
            log.error(ExceptionUtils.getStackTrace(e))
            throw e
        } finally {
            obj?.close()
        }
    }

    fun getPreSignedUrl(url: String, timeValidityInSecs: Long): String {
        log.info("Inside getPreSignedUrl")
        val path = extractPathFromUrl(url)
        val expiration = Date()
        var expTimeMillis = expiration.time
        expTimeMillis += 1000L * timeValidityInSecs
        expiration.time = expTimeMillis
        val bucketName = extractBucketFromUrl(url)
        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, path)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration)
        return s3client.generatePresignedUrl(generatePresignedUrlRequest).toString()
    }

    @Cacheable(CacheNames.PRE_SIGNED_URL_12_HOURS, unless = "#result==null")
    fun getPreSignedUrlFor12Hours(url: String): String {
        log.info("inside getPreSignedUrlFor12Hours $url")
        return getPreSignedUrl(url, 60 * 60 * 12)
    }

    @Throws(IOException::class)
    fun getFileByUrl(url: String): S3ObjectMeta {
        val path = extractPathFromUrl(url)
        val bucket = extractBucketFromUrl(url)
        return getFileByPath(bucket,path)
    }

    fun createZipAndDownload(urls: List<String>, id: Long, identifiers: MutableMap<String?, String?>? = null) : S3ObjectMeta{
        FileUtils.createDirectory("geebee")
        val dir = FileUtils.getTempFileName("geebee"+"/"+id.toString())
        val directory =  File(dir)
        directory.mkdir()

        for (url in urls) {
            var obj: S3Object? = null
            try{
                log.info("processing url: $url")
                val keyName = extractPathFromUrl(url)
                obj = s3client.getObject(bucketName, keyName)
                val stream = obj.objectContent
                val fileNameSplit = url.split("/")
                var fileName = fileNameSplit.get(fileNameSplit.size - 2)
                if(!identifiers.isNullOrEmpty())
                fileName = "${identifiers[url]}_${fileName}"
                val out: OutputStream = FileOutputStream("$dir/${fileName}_${url.split("/").last()}")
                IOUtils.copy(stream, out)
            }catch (e: Exception) {
                log.error(ExceptionUtils.getStackTrace(e))
                throw e
            } finally {
                obj?.close()
            }
        }

        try {
            zipDirectory(directory, "$dir.zip")
            val encoded = Files.readAllBytes(Paths.get("$dir.zip"))
            return S3ObjectMeta(contentType = "application/zip",data = encoded,fileName = "$dir.zip".split("/").last() )
        } finally {
            FileUtils.deleteSilently(directory)
            FileUtils.deleteSilently(File("$dir.zip"))
        }

    }
    fun createZipAndUpload(urls: List<String>, id: Long, filePath: String ,fileName: String, identifiers: MutableMap<String?, String?>? = null) : String?{
        FileUtils.createDirectory("geebee")
        val dir = FileUtils.getTempFileName("geebee"+"/"+id.toString())
        val directory =  File(dir)
        directory.mkdir()

        for (url in urls) {
            var obj: S3Object? = null
            try{
                log.info("processing url: $url")
                val keyName = extractPathFromUrl(url)
                val bucketName = extractBucketFromUrl(url)
                obj = s3client.getObject(bucketName, keyName)
                val stream = obj.objectContent
                val fileNameSplit = url.split("/")
                var fileName = fileNameSplit.get(fileNameSplit.size - 2)
                if(!identifiers.isNullOrEmpty())
                    fileName = "${identifiers[url]}_${fileName}"
                val out: OutputStream = FileOutputStream("$dir/${fileName}_${url.split("/").last()}")
                IOUtils.copy(stream, out)
            }catch (e: Exception) {
                log.error(ExceptionUtils.getStackTrace(e))
                throw e
            } finally {
                obj?.close()
            }

        }
        try {
            zipDirectory(directory, "$dir.zip")
            return uploadFileTos3bucket("$filePath/$fileName", File("$dir.zip"))
        } finally {
            FileUtils.deleteSilently(directory)
            FileUtils.deleteSilently(File("$dir.zip"))
        }
    }

    private fun zipDirectory(dir: File, zipDirName: String) {
        try {
            val filesListInDir =  populateFilesList(dir)
            //now zip files one by one
//create ZipOutputStream to write to the zip file
            val fos = FileOutputStream(zipDirName)
            val zos = ZipOutputStream(fos)
            try {
                for (filePath in filesListInDir) {
                    println("Zipping $filePath")
                    //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                    val ze = ZipEntry(filePath.substring(dir.absolutePath.length + 1, filePath.length))
                    zos.putNextEntry(ze)
                    //read the file and write to ZipOutputStream
                    val fis = FileInputStream(filePath)
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (fis.read(buffer).also { len = it } > 0) {
                        zos.write(buffer, 0, len)
                    }
                    zos.closeEntry()
                    fis.close()
                }
            } catch (ex: Exception){
                log.error(ex.getTrace())
            } finally {
                zos.close()
                fos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun populateFilesList(dir: File): List<String> {
        val filesListInDir = mutableListOf<String>()
        val files = dir.listFiles()
        for (file in files) {
            if (file.isFile) filesListInDir.add(file.absolutePath) else populateFilesList(file)
        }
        return filesListInDir
    }

    fun getFileByPath(bucketName: String, path: String): S3ObjectMeta {
        var obj: S3Object? = null
        return try {
            obj = s3client.getObject(bucketName, path)
            obj.objectMetadata.contentEncoding
            val stream = obj.objectContent
            val content = IOUtils.toByteArray(stream)
            val s3ObjectMeta = S3ObjectMeta(data = content, contentType = obj.objectMetadata.contentType, fileName = path.split("/".toRegex()).toTypedArray()[path.split("/".toRegex()).toTypedArray().size - 1])
            s3ObjectMeta
        } catch (e: Exception) {
            log.error(ExceptionUtils.getStackTrace(e))
            throw e
        }finally {
            obj?.close()
        }
    }

    @Throws(Exception::class)
    fun getStreamByUrl(url: String): S3ObjectInputStream {
        val path = extractPathFromUrl(url)
        var obj: S3Object? = null
        return try {
            obj = s3client.getObject(bucketName, path)
            obj.objectMetadata.contentEncoding
            obj.objectContent
        }catch (e: Exception) {
            log.error(ExceptionUtils.getStackTrace(e))
            throw e
        }finally {
            obj?.close()
        }

    }

    @Throws(IOException::class)
    fun uploadFile(file: File, documentsType: String, id: String, assetType: String): String {
        var fileUrl: String? = ""
        fileUrl = try {
            val fileName: String = file.name.replace(" ", "_")
            val path = "${documentsType}/$id/$assetType/$fileName"
            uploadFileTos3bucket(path, file)
        } catch (e: Exception) {
            log.error(ExceptionUtils.getStackTrace(e))
            throw e
        }
        return fileUrl.orEmpty()
    }

    @Throws(IOException::class)
    fun uploadFile(multipartFile: MultipartFile, assetType: String, customName: String): String {
        var fileUrl: String? = ""
        fileUrl = try {
            val file: File = FileUtils.convertMultiPartToFile(multipartFile)
            val fileName: String = FileUtils.generateFileName(multipartFile, customName)
            val path = "$assetType/$fileName"
            uploadFileTos3bucket(path, file)
        } catch (e: Exception) {
            log.error(ExceptionUtils.getStackTrace(e))
            throw e
        }
        return fileUrl.orEmpty()
    }

    @Throws(IOException::class)
    fun uploadFileInCustomBucket(multipartFile: MultipartFile, folder: String, bucket: String? = null): String {
        var fileUrl: String? = ""
        fileUrl = try {
            val file: File = FileUtils.convertMultiPartToFile(multipartFile)
            val fileName: String = FileUtils.generateFileName(multipartFile)
            val path = "$folder/$fileName"
            if (bucket == null)
                uploadFileTos3bucketWithPublicRead(path, file)
            else
                uploadFileTos3bucketWithPublicRead(bucket, path, file)
        } catch (e: Exception) {
            log.error(ExceptionUtils.getStackTrace(e))
            throw e
        }
        return fileUrl.orEmpty()
    }

    @Throws(IOException::class)
    fun uploadFileTypeInCustomBucket(file: File, folder: String, bucket: String? = null): String {
        var fileUrl: String? = ""
        fileUrl = try {
            val fileName: String = FileUtils.generateFileName(file)
            val path = "$folder/$fileName"
            if (bucket == null)
                uploadFileTos3bucketWithPublicRead(path, file)
            else
                uploadFileTos3bucketWithPublicRead(bucket, path, file)
        } catch (e: Exception) {
            log.error(ExceptionUtils.getStackTrace(e))
            throw e
        }
        return fileUrl.orEmpty()
    }

    fun uploadFileInCustomBucketAndDeleteTempFile(file: File, folder: String, bucket: String? = null): String {
        val fileUrl = uploadFileTypeInCustomBucket(file, folder, bucket)
        FileUtils.deleteSilently(file)
        return fileUrl
    }

    fun uploadFileTos3bucket(fileName: String, file: File): String? {
        s3client.putObject(PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.BucketOwnerFullControl))
        return try {
            URLDecoder.decode(s3client.getUrl(bucketName, fileName).toString(), StandardCharsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            log.error(ExceptionUtils.getStackTrace(e))
            null
        }
    }

    fun uploadFileTos3bucketWithPublicRead(fileName: String, file: File): String? {
        return uploadFileTos3bucketWithPublicRead(bucketName!!, fileName, file)
    }

    fun uploadFileTos3bucketWithPublicRead(bucket: String, fileName: String, file: File): String? {
        s3client.putObject(PutObjectRequest(bucket, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead))
        return try {
            URLDecoder.decode(s3client.getUrl(bucket, fileName).toString(), StandardCharsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            log.error(ExceptionUtils.getStackTrace(e))
            null
        }
    }

    fun extractPathFromUrl(url: String): String {
        val regex = "https:\\/\\/([A-Za-z0-9\\-]*)\\.([A-Za-z0-9\\-\\.]*)\\/(.*)"
        val pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val matcher = pattern.matcher(url)
        while (matcher.find()) {
            return if (matcher.groupCount() < 3) {
                ""
            } else {
                matcher.group(3)
            }
        }
        return ""
    }

    private fun extractBucketFromUrl(url: String): String {
        val regex = "https:\\/\\/([A-Za-z0-9\\-]*)\\.([A-Za-z0-9\\-\\.]*)\\/(.*)"
        val pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val matcher = pattern.matcher(url)
        while (matcher.find()) {
            return if (matcher.groupCount() < 1) {
                ""
            } else {
                matcher.group(1)
            }
        }
        return ""
    }

    @Throws(IOException::class)
    private fun getS3ObjectByUrl(url: String): S3Object {
        val path = extractPathFromUrl(url)
        return s3client.getObject(bucketName, path)
    }

    @Throws(Exception::class)
    fun deleteS3Object(url: String) {
        var s3Object: S3Object? = null
        try {
            s3Object = getS3ObjectByUrl(url)
            s3client.deleteObject(DeleteObjectRequest(bucketName, s3Object.key))
        } catch (ex: Exception) {
            log.error("Error in deleteS3Object ${ex.getTrace()}")
            throw ex
        }finally {
            s3Object?.close()
        }
    }

    fun getBucketNameFromUrl(url: String): String {
        return extractBucketFromUrl(url)
    }

    fun getFileNameFromUrl(url: String): String? {
        val regex = "[^\\/]+$"
        return Regex(regex).find(url)?.value
    }
}


