package com.daniel.infosumbar.model

data class Data(
    var tanggal: String = "",
    var jam9: DataValue = DataValue(),
    var jam13: DataValue = DataValue(),
    var jam16: DataValue = DataValue(),
    var jam19: DataValue = DataValue(),
    var jam22: DataValue = DataValue()
)

