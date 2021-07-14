package m.kampukter.smarthomemanagement.data

import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus

data class UnitInfoView(
    val id: String,
    val name: String?,
    val url: String,
    val description: String?,
    var wsConnectionStatus: WSConnectionStatus?
)
