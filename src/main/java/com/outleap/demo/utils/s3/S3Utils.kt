package com.outleap.demo.utils.s3

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.util.IOUtils
import com.outleap.demo.utils.exceptions.getTrace
import com.outleap.demo.utils.logger
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

@Component
class S3Utils(
    @param:Qualifier("IAM") private val s3ClientApSouth1: AmazonS3
) {

    private val log = logger()

    fun getFileByteArrayByUrl(url: String, region: Regions): ByteArray {
        val uri = URI(url)
        val bucketName = uri.host.split('.').first()
        val objectKey = uri.path.substring(1)
        val s3Client = s3ClientApSouth1
        var s3Object: S3Object? = null
        return try {
            s3Object = s3Client.getObject(bucketName, objectKey)
            IOUtils.toByteArray(s3Object.objectContent)
        } catch (ex: Exception) {
            log.error("Error inside getFileByteArrayByUrl $url ${ex.getTrace()}")
            throw ex
        } finally {
            s3Object?.close()
        }
    }

    fun uploadFileTos3bucket(bucketName: String, fileName: String, file: File, region: Regions): String? {
        val s3Client = s3ClientApSouth1
        s3Client.putObject(
            PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.BucketOwnerFullControl)
        )
        return try {
            URLDecoder.decode(s3Client.getUrl(bucketName, fileName).toString(), StandardCharsets.UTF_8.name())
        } catch (ex: Exception) {
            log.error("Error inside uploadFileTos3bucket $bucketName $fileName ${ex.getTrace()}")
            throw ex
        }
    }

    fun getFileByPath(bucketName: String, path: String, region: Regions = Regions.AP_SOUTH_1): S3ObjectMeta {
        var obj: S3Object? = null
        return try {
            val s3Client = s3ClientApSouth1
            obj = s3Client.getObject(bucketName, path)
            obj.objectMetadata.contentEncoding
            val stream = obj.objectContent
            val content = IOUtils.toByteArray(stream)
            S3ObjectMeta(
                data = content,
                contentType = obj.objectMetadata.contentType,
                fileName = path.split("/").lastOrNull().orEmpty()
            )
        } catch (e: Exception) {
            log.error(ExceptionUtils.getStackTrace(e))
            throw e
        } finally {
            obj?.close()
        }
    }

    @Throws(IOException::class)
    fun getFileByUrl(url: String): S3ObjectMeta {
        val path = extractPathFromUrl(url)
        val bucket = extractBucketFromUrl(url)
        return getFileByPath(bucket, path)
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
}