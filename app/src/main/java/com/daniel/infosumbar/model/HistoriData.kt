package com.daniel.infosumbar.model

data class HistoriData(
    var no_transaksi: String = "",
    var timestamp: String = "",
    @JvmField
    var isBooked: Boolean = false,
    @JvmField
    var isConfirm: Boolean = false,
    var uid: String = "",
    var nama: String = "",
    var email: String = "",
    var no_hp: String = "",
    var jenis_pekerjaan: String = "",
    var jenis_iklan: String = "",
    var bukti_pembayaran: String? = null,
    var tagihan: String = "",
    @JvmField
    var isSingle: Boolean = false,
    var singleDocumentReference: String = "",
    var fieldRoute: String = ""
)