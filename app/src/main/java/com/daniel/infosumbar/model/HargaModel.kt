package com.daniel.infosumbar.model

data class HargaModel(
    var paket1: String = "",
    var paket2: String = "",
    var paket3: String = "",
    var jam9: HashMap<String, String> = HashMap(),
    var jam13: HashMap<String, String> = HashMap(),
    var jam16: HashMap<String, String> = HashMap(),
    var jam19: HashMap<String, String> = HashMap(),
    var jam22: HashMap<String, String> = HashMap(),
    var facebook: HashMap<String, String> = HashMap(),
    var twitter: HashMap<String, String> = HashMap()

)