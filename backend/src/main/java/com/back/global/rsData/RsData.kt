package com.back.global.rsData

import com.fasterxml.jackson.annotation.JsonIgnore

@JvmRecord
data class RsData<T> (
    val resultCode: String,
    @field:JsonIgnore val statusCode: Int = resultCode.substringBefore("-").toInt(),
    val msg: String,
    val data: T? = null
)

