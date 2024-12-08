package com.outleap.demo.utils.gson

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne


class ExcludeProxiedFieldsStrategy : ExclusionStrategy {
    override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
        return fieldAttributes.getAnnotation(ManyToOne::class.java) != null ||
                fieldAttributes.getAnnotation(OneToOne::class.java) != null ||
                fieldAttributes.getAnnotation(ManyToMany::class.java) != null ||
                fieldAttributes.getAnnotation(OneToMany::class.java) != null
    }

    override fun shouldSkipClass(type: Class<*>?): Boolean {
        return false
    }
}