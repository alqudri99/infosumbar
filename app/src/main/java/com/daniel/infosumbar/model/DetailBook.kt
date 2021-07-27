package com.daniel.infosumbar.model

data class DetailBook(
    val timestamp: Long? = null,
    var uid: String = "",
    @JvmField
    var isConfirm: Boolean = false,
    @JvmField
    var isBooked: Boolean = false
)