package com.outleap.demo.utils.gson

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.outleap.demo.utils.DATE_TIME_PATTERN

inline fun <reified T> String.toGsonWithNulls(): T {
    val type = object : TypeToken<T>() {}.type
    return getGsonWithNulls().fromJson(this, type)
}

inline fun <reified T> String.toGson(): T {
    val type = object : TypeToken<T>() {}.type
    return getGson().fromJson(this, type)
}

inline fun <reified T> Any.toGsonWithNulls(): T {
    val type = object : TypeToken<T>() {}.type
    return getGsonWithNulls().fromJson(this.toJsonWithNulls(), type)
}

inline fun <reified T> Any.toGson(): T {
    val type = object : TypeToken<T>() {}.type
    return getGson().fromJson(this.toJson(), type)
}

fun Any?.toJsonWithNulls(): String {
    return getGsonWithNulls().toJson(this)
}

fun Any?.toJson(): String {
    return getGson().toJson(this)
}

fun getGson(): Gson = Gson().newBuilder()
    .setExclusionStrategies(ExcludeProxiedFieldsStrategy())
    .setDateFormat(DATE_TIME_PATTERN)
    .create()
fun getGsonWithNulls(): Gson = Gson().newBuilder()
    .setExclusionStrategies(ExcludeProxiedFieldsStrategy())
    .setDateFormat(DATE_TIME_PATTERN)
    .serializeNulls().create()

fun <T> T.serializeToMap(): Map<String, Any?> {
    return convert()
}

inline fun <I, reified O> I.convert(): O {
    val json = getGsonWithNulls().toJson(this)
    return getGsonWithNulls().fromJson(json, object : TypeToken<O>() {}.type)
}