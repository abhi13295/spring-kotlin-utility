package com.outleap.demo.utils.file

import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.HeaderColumnNameMappingStrategy
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.opencsv.enums.CSVReaderNullFieldIndicator
import java.io.*

object CSVUtils {
    @Throws(IOException::class)
    fun <T> convertToBean(file: File, bean: Class<T>): List<T>? {
        var csvData: List<T>?
        FileReader(file).use { reader ->
            val strategy =
                HeaderColumnNameMappingStrategy<T>()
            strategy.type = bean
            val cb = CsvToBeanBuilder<T>(reader)
                .withType(bean)
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .withMappingStrategy(strategy)
                .build()
            csvData = cb.parse()
        }
        return csvData
    }

    fun <T> convertToBean(byteArray: ByteArray, bean: Class<T>): List<T>? {
        val inputStream = ByteArrayInputStream(byteArray)
        val reader = InputStreamReader(inputStream)
        return try {
            val strategy = HeaderColumnNameMappingStrategy<T>()
            strategy.type = bean
            val cb = CsvToBeanBuilder<T>(reader)
                .withType(bean)
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .withMappingStrategy(strategy)
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .build()
            val csvData = cb.parse()
            csvData
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error processing CSV to Bean", e)
        } finally {
            reader.close()
            inputStream.close()
        }
    }

    inline fun <reified T> createCSV(data: List<T>, fileName: String): File {
        val mappingStrategy = CustomColumnPositionMappingStrategy<T>()
        mappingStrategy.type = T::class.java
        val newCsvFile = FileUtils.createTempFile(fileName)
        FileWriter(newCsvFile).use { writer ->
            val beanWriter = StatefulBeanToCsvBuilder<T>(writer)
                .withMappingStrategy(mappingStrategy)
                .build()
            beanWriter.write(data)
        }
        return newCsvFile
    }
}