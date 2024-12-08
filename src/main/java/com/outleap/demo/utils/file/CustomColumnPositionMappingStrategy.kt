package com.outleap.demo.utils.file

import com.opencsv.bean.BeanField
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvBindByName
import com.opencsv.exceptions.CsvRequiredFieldEmptyException
import org.apache.commons.lang3.StringUtils

open class CustomColumnPositionMappingStrategy<T> : ColumnPositionMappingStrategy<T>() {
    @Throws(CsvRequiredFieldEmptyException::class)
    override fun generateHeader(bean: T): Array<String> {
        val headersAsPerFieldName = fieldMap.generateHeader(bean) // header name based on field name
        val header = Array(headersAsPerFieldName.size){ StringUtils.EMPTY}
        for (i in headersAsPerFieldName.indices) {
            val beanField = findField(i)
            var columnHeaderName = extractHeaderName(beanField) // header name based on @CsvBindByName annotation
            if (columnHeaderName.isEmpty()) // No @CsvBindByName is present
                columnHeaderName = headersAsPerFieldName[i] // defaults to header name based on field name
            header[i] = columnHeaderName
        }
        headerIndex.initializeHeaderIndex(header)
        return header
    }

    private fun extractHeaderName(beanField: BeanField<T, Int>?): String {
        if (beanField?.field == null || beanField.field.getDeclaredAnnotationsByType(CsvBindByName::class.java).isEmpty()) {
            return StringUtils.EMPTY
        }
        val bindByNameAnnotation = beanField.field.getDeclaredAnnotationsByType(CsvBindByName::class.java)[0]
        return bindByNameAnnotation.column
    }

}